package com.firefly.core.support.xml.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.firefly.core.support.exception.BeanDefinitionParsingException;
import com.firefly.utils.dom.Dom;

public class XmlNodeStateMachine {
	private static Logger log = LoggerFactory.getLogger(XmlNodeStateMachine.class);

	public static Object stateProcessor(Element ele, Dom dom) {
		String elementName = ele.getNodeName() != null ? ele.getNodeName()
				: ele.getLocalName();
		XmlNodeParser xmlNodeParser = XmlNodeParserFactory.getParser(elementName);
		if(xmlNodeParser == null)
			error("Unknown property sub-element: [" + ele.getNodeName() + "]");
		Object ret = xmlNodeParser.parse(ele, dom);
		return ret;
	}

	private static void error(String msg) {
		log.error(msg);
		throw new BeanDefinitionParsingException(msg);
	}
}
