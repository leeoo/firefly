package com.firefly.core.support.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.BeanReader;
import com.firefly.core.support.exception.BeanDefinitionParsingException;
import com.firefly.utils.ReflectUtils;
import com.firefly.utils.StringUtils;
import com.firefly.utils.dom.DefaultDom;
import com.firefly.utils.dom.Dom;

/**
 * 读取Xml文件
 * 
 * @author 须俊杰
 * @date 2011-3-3
 */
public class XmlBeanReader implements BeanReader {

	private static Logger log = LoggerFactory.getLogger(XmlBeanReader.class);

	public static final String BEAN_ELEMENT = "bean";
	public static final String BEAN_REF_ATTRIBUTE = "bean";
	public static final String ID_ATTRIBUTE = "id";
	public static final String CLASS_ATTRIBUTE = "class";
	public static final String PROPERTY_ELEMENT = "property";
	public static final String NAME_ATTRIBUTE = "name";
	public static final String REF_ATTRIBUTE = "ref";
	public static final String VALUE_ATTRIBUTE = "value";
	public static final String TYPE_ATTRIBUTE = "type";
	public static final String VALUE_TYPE_ATTRIBUTE = "value-type";
	public static final String LIST_ELEMENT = "list";
	public static final String MAP_ELEMENT = "map";
	protected List<BeanDefinition> beanDefinitions;

	public XmlBeanReader() {
		this(null);
	}

	public XmlBeanReader(String file) {
		beanDefinitions = new ArrayList<BeanDefinition>();
		Dom dom = new DefaultDom();
		// 获得Xml文档对象
		Document doc = dom.getDocument(file == null ? "firefly.xml" : file);

		// 得到根节点
		Element root = dom.getRoot(doc);

		// 得到所有bean节点
		List<Element> beansList = dom.elements(root, BEAN_ELEMENT);

		// 迭代beans列表
		if (beansList != null) {
			for (Element bean : beansList) {
				beanDefinitions.add(parseBeanElement(bean, dom));
			}
		}
	}

	/**
	 * 解析xml
	 * 
	 * @return
	 */
	@Override
	public List<BeanDefinition> loadBeanDefinitions() {
		return beanDefinitions;
	}

	protected XmlBeanDefinition parseBeanElement(Element bean, Dom dom) {
		XmlBeanDefinition xmlBeanDefinition = new XmlGenericBeanDefinition();
		// 获取基本属性
		String id = bean.getAttribute(ID_ATTRIBUTE);
		String className = bean.getAttribute(CLASS_ATTRIBUTE);
		xmlBeanDefinition.setId(id);
		xmlBeanDefinition.setClassName(className);

		// 实例化对象
		Class<?> clazz = null;
		Object obj = null;
		log.info("classes [{}]", className);
		try {
			clazz = XmlBeanReader.class.getClassLoader().loadClass(className);
			obj = clazz.newInstance();
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		xmlBeanDefinition.setObject(obj);

		// 取得接口名称
		Set<String> names = ReflectUtils.getInterfaceNames(clazz);
		xmlBeanDefinition.setInterfaceNames(names);
		log.debug("class [{}] names size [{}]", className, names.size());

		// 获取所有property
		List<Element> properties = dom.elements(bean, PROPERTY_ELEMENT);

		// 迭代property列表
		if (properties != null) {
			for (Element property : properties) {
				String name = property.getAttribute(NAME_ATTRIBUTE);

				boolean hasValueAttribute = property
						.hasAttribute(VALUE_ATTRIBUTE);
				boolean hasRefAttribute = property.hasAttribute(REF_ATTRIBUTE);

				// 只能有一个子元素: ref, value, list, etc.
				NodeList nl = property.getChildNodes();
				Element subElement = null;
				for (int i = 0; i < nl.getLength(); ++i) {
					Node node = nl.item(i);
					if (node instanceof Element) {
						if (subElement != null) {
							error(name
									+ " must not contain more than one sub-element");
						} else {
							subElement = (Element) node;
						}
					}
				}

				if (hasValueAttribute
						&& hasRefAttribute
						|| ((hasValueAttribute || hasRefAttribute) && subElement != null)) {
					error(name
							+ " is only allowed to contain either 'ref' attribute OR 'value' attribute OR sub-element");
				}

				if (hasValueAttribute) {
					// 普通赋值
					String value = property.getAttribute(VALUE_ATTRIBUTE);
					if (!StringUtils.hasText(value)) {
						error(name + " contains empty 'value' attribute");
					}
					xmlBeanDefinition.getProperties().put(name,
							new ManagedValue(value));
				} else if (hasRefAttribute) {
					// 依赖其他bean
					String ref = property.getAttribute(REF_ATTRIBUTE);
					if (!StringUtils.hasText(ref)) {
						error(name + " contains empty 'ref' attribute");
					}
					xmlBeanDefinition.getProperties().put(name,
							new ManagedRef(ref));
				} else if (subElement != null) {
					// 处理子元素
					Object subEle = parsePropertySubElement(subElement, dom);
					xmlBeanDefinition.getProperties().put(name, subEle);
				} else {
					error(name + " must specify a ref or value");
					return null;
				}
			}
		}
		return xmlBeanDefinition;
	}

	/**
	 * 解析子元素
	 * 
	 * @param ele
	 * @return
	 */
	protected Object parsePropertySubElement(Element ele, Dom dom) {
		if (nodeNameEquals(ele, REF_ATTRIBUTE)) { // ref
			if (ele.hasAttribute(BEAN_REF_ATTRIBUTE)) {
				String refText = ele.getAttribute(BEAN_REF_ATTRIBUTE);
				if (StringUtils.hasText(refText)) {
					ManagedRef ref = new ManagedRef();
					ref.setBeanName(refText);
					return ref;
				} else {
					error("<ref> element contains empty target attribute");
					return null;
				}
			} else {
				error("'bean' is required for <ref> element");
				return null;
			}
		} else if (nodeNameEquals(ele, VALUE_ATTRIBUTE)) { // value
			ManagedValue typedValue = new ManagedValue();
			String value = dom.getTextValue(ele);
			String typeName = null;
			if (ele.hasAttribute(TYPE_ATTRIBUTE)) {
				// 如果有type属性
				typeName = ele.getAttribute(TYPE_ATTRIBUTE);
				if (typeName == null) {
					error("<value> element contains empty target attribute");
					return null;
				}
			}

			typedValue.setValue(value);
			typedValue.setTypeName(typeName);
			return typedValue;
		} else if (nodeNameEquals(ele, LIST_ELEMENT)) { // list
			return parseListElement(ele, dom);
		} else if (nodeNameEquals(ele, MAP_ELEMENT)) { // map

		} else {
			error("Unknown property sub-element: [" + ele.getNodeName() + "]");
			return null;
		}

		return null;
	}

	/**
	 * 解析list元素
	 * 
	 * @param ele
	 * @return
	 */
	protected List<Object> parseListElement(Element ele, Dom dom) {
		String typeName = ele.getAttribute(VALUE_TYPE_ATTRIBUTE);
		ManagedList<Object> target = new ManagedList<Object>();
		target.setTypeName(typeName);
		List<Element> elements = dom.elements(ele);
		for (Element e : elements) {
			target.add(parsePropertySubElement(e, dom));
		}
		return target;
	}

	/**
	 * 处理异常
	 * 
	 * @param msg
	 *            异常信息
	 */
	private void error(String msg) {
		log.error(msg);
		throw new BeanDefinitionParsingException(msg);
	}

	private boolean nodeNameEquals(Node node, String desiredName) {
		return desiredName.equals(node.getNodeName())
				|| desiredName.equals(node.getLocalName());
	}
}
