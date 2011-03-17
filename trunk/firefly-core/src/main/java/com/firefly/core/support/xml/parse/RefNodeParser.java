package com.firefly.core.support.xml.parse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import static com.firefly.core.support.xml.parse.XmlNodeConstants.*;

import com.firefly.core.support.exception.BeanDefinitionParsingException;
import com.firefly.core.support.xml.ManagedRef;
import com.firefly.utils.StringUtils;
import com.firefly.utils.dom.Dom;

public class RefNodeParser implements XmlNodeParser {

	private static Logger log = LoggerFactory.getLogger(RefNodeParser.class);

	@Override
	public Object parse(Element ele, Dom dom) {
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
	}

	protected void error(String msg) {
		log.error(msg);
		throw new BeanDefinitionParsingException(msg);
	}

}
