package com.firefly.core.support.xml;

import static com.firefly.core.support.xml.parse.XmlNodeConstants.BEAN_ELEMENT;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.firefly.core.support.AbstractBeanReader;
import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.xml.parse.XmlNodeStateMachine;
import com.firefly.utils.dom.DefaultDom;
import com.firefly.utils.dom.Dom;

/**
 * 读取Xml文件
 * 
 * @author 须俊杰, alvinqiu
 */
public class XmlBeanReader extends AbstractBeanReader {

	public XmlBeanReader() {
		this(null);
	}

	public XmlBeanReader(String file) {
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
				beanDefinitions.add((BeanDefinition) XmlNodeStateMachine
						.stateProcessor(ele, dom));
			}
		}
	}
}
