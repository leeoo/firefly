package com.firefly.core.support;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.core.support.exception.BeanDefinitionParsingException;

public class AbstractBeanReader implements BeanReader {
	private static Logger log = LoggerFactory
			.getLogger(AbstractBeanReader.class);
	protected List<BeanDefinition> beanDefinitions;

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

	@Override
	public BeanDefinition findBeanDefinition(String key) {
		for (BeanDefinition beanDefinition : beanDefinitions) {
			if (key.equals(beanDefinition.getId())) {
				return beanDefinition;
			} else if (key.equals(beanDefinition.getClassName())) {
				return beanDefinition;
			} else {
				for (String interfaceName : beanDefinition.getInterfaceNames()) {
					if (key.equals(interfaceName))
						return beanDefinition;
				}
			}
		}
		return null;
	}
}
