package com.firefly.core.support.xml.parse;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.firefly.core.support.exception.BeanDefinitionParsingException;
import com.firefly.core.support.xml.ManagedArray;
import com.firefly.utils.dom.Dom;

public class ArrayNodeParser implements XmlNodeParser {

	private static Logger log = LoggerFactory.getLogger(ArrayNodeParser.class);
	@Override
	public Object parse(Element ele, Dom dom) {
		ManagedArray<Object> target = new ManagedArray<Object>();
		List<Element> elements = dom.elements(ele);
		for (Element e : elements) {
			target.add(XmlNodeStateMachine.stateProcessor(e, dom));
		}
		return target;
	}

	protected void error(String msg) {
		log.error(msg);
		throw new BeanDefinitionParsingException(msg);
	}
}
