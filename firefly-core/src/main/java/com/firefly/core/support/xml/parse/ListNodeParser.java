package com.firefly.core.support.xml.parse;

import static com.firefly.core.support.xml.parse.XmlNodeConstants.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.firefly.core.support.exception.BeanDefinitionParsingException;
import com.firefly.core.support.xml.ManagedList;
import com.firefly.utils.dom.Dom;

public class ListNodeParser implements XmlNodeParser {

	private static Logger log = LoggerFactory.getLogger(ListNodeParser.class);

	@Override
	public Object parse(Element ele, Dom dom) {
		String typeName = ele.getAttribute(TYPE_ATTRIBUTE);
		ManagedList<Object> target = new ManagedList<Object>();
		target.setTypeName(typeName);
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
