package com.firefly.core.support.xml;

import static com.firefly.core.support.xml.parse.XmlNodeConstants.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.BeanReader;
import com.firefly.core.support.exception.BeanDefinitionParsingException;
import com.firefly.core.support.xml.parse.XmlNodeParserFactory;
import com.firefly.utils.dom.DefaultDom;
import com.firefly.utils.dom.Dom;

/**
 * 读取Xml文件
 *
 * @author 须俊杰, alvinqiu
 */
public class XmlBeanReader implements BeanReader {

	private static Logger log = LoggerFactory.getLogger(XmlBeanReader.class);
	protected List<BeanDefinition> beanDefinitions;
	protected Set<String> idSet;

	public XmlBeanReader() {
		this(null);
	}

	public XmlBeanReader(String file) {
		idSet = new HashSet<String>();
		beanDefinitions = new ArrayList<BeanDefinition>();
		Dom dom = new DefaultDom();
		// 为多文件载入做准备

		// 获得Xml文档对象
		Document doc = dom.getDocument(file == null ? "firefly.xml" : file);
		// 得到根节点
		Element root = dom.getRoot(doc);
		// 得到所有bean节点
		List<Element> beansList = dom.elements(root, BEAN_ELEMENT);
		// 迭代beans列表
		if (beansList != null) {
			for (Element ele : beansList) {
				beanDefinitions.add((BeanDefinition) XmlNodeParserFactory
						.getParser(BEAN_ELEMENT).parse(ele, dom));
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

	/**
	 * 处理异常
	 *
	 * @param msg
	 *            异常信息
	 */
	protected void error(String msg) {
		log.error(msg);
		throw new BeanDefinitionParsingException(msg);
	}
}
