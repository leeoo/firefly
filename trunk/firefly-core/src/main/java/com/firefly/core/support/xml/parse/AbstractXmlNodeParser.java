package com.firefly.core.support.xml.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.firefly.core.support.exception.BeanDefinitionParsingException;

public abstract class AbstractXmlNodeParser implements XmlNodeParser {

	protected static Logger log = LoggerFactory.getLogger(AbstractXmlNodeParser.class);

	protected void error(String msg) {
		log.error(msg);
		throw new BeanDefinitionParsingException(msg);
	}
}
