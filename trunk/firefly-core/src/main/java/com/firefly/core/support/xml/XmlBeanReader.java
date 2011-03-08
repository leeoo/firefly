package com.firefly.core.support.xml;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.BeanReader;

/**
 * 读取Xml文件
 * @author 杰然不同
 * @date 2011-3-3
 * @Version 1.0
 */
public class XmlBeanReader implements BeanReader{

	private static Logger log = LoggerFactory.getLogger(XmlBeanReader.class);

	private final String filename;

	public XmlBeanReader(String filename){
		this.filename = filename;
	}

	/**
	 * 解析xml
	 * @return
	 */
	@Override
	public List<BeanDefinition> loadBeanDefinitions() {
		List<BeanDefinition> beanList = new ArrayList<BeanDefinition>();

		try {
			// 获得Xml文档对象
			Document doc = readDocument(this.filename);

			// 得到所有bean节点
			XPathFactory xpf = XPathFactory.newInstance();
			XPath xPath = xpf.newXPath();
			NodeList beans = (NodeList)xPath.evaluate("beans/bean", doc, XPathConstants.NODESET);

			// 迭代所有子节点
			if(beans != null){
				for(int i = 0;i < beans.getLength();++i){
					XmlBeanDefinition xmlBeanDefinition = new XmlGenericBeanDefinition();

					Node bean = beans.item(i);
					String id = bean.getAttributes().getNamedItem("id").getNodeValue();
					String className = bean.getAttributes().getNamedItem("class").getNodeValue();
					xmlBeanDefinition.setId(id);
					xmlBeanDefinition.setClassName(className);

					Class<?> clazz = null;
					Object obj = null;
					log.info("classes [{}]", className);
					try {
						clazz = XmlBeanReader.class.getClassLoader().loadClass(className);
						//Class.forName(xmlBeanDefinition.getClassName());
						obj = clazz.newInstance();
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					} catch (InstantiationException e) {
						throw new RuntimeException(e);
					} catch (IllegalAccessException e) {
						throw new RuntimeException(e);
					}

					xmlBeanDefinition.setObject(obj);

					Set<String> names = getInterfaceNames(clazz);
					xmlBeanDefinition.setInterfaceNames(names);
					log.debug("class [{}] names size [{}]", className, names.size());

					// 获取所有property
					NodeList properties = bean.getChildNodes();
					for(int j = 0;j < properties.getLength();++j){
						Node property = properties.item(j);

						// 判断是否是元素节点(W3C里面空白也算节点)
						if(property.getNodeType() == Node.ELEMENT_NODE){
							String name = property.getAttributes().getNamedItem("name").getNodeValue();

							// 普通赋值
							Node value = property.getAttributes().getNamedItem("value");
							if(value != null)
								xmlBeanDefinition.getProperties().put(name, value.getNodeValue());

							// 依赖其他bean
							Node ref = property.getAttributes().getNamedItem("ref");
							if(ref != null)
								xmlBeanDefinition.getProperties().put(name, ref.getNodeValue());
						}
					}


					beanList.add(xmlBeanDefinition);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return beanList;
	}

	/**
	 * 根据文件读取Document
	 * @Date 2011-3-3
	 * @param filePath
	 * @return 文档对象
	 * @throws DocumentException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws FileNotFoundException
	 */
	private Document readDocument(String fileName) throws ParserConfigurationException, FileNotFoundException, SAXException, IOException{

		// 得到dom解析器工厂实例
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		// 得到dom解析器
		DocumentBuilder dbd = dbf.newDocumentBuilder();

		Document doc = dbd.parse(XmlBeanReader.class.getResourceAsStream(fileName == null ? "/firefly.xml" : fileName));

		return doc;
	}

	protected Set<String> getInterfaceNames(Class<?> c) {
		Class<?>[] interfaces = c.getInterfaces();
		Set<String> names = new HashSet<String>();
		for (Class<?> i : interfaces) {
			names.add(i.getName());
		}
		return names;
	}

	public static void main(String[] args) {
		XmlBeanReader xmlBeanReader = new XmlBeanReader(null);
		xmlBeanReader.loadBeanDefinitions();
	}
}
