package com.firefly.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.BeanReader;
import com.firefly.utils.VerifyUtils;

abstract public class AbstractApplicationContext implements ApplicationContext {

	protected Map<String, Object> map = new HashMap<String, Object>();
	protected BeanReader beanReader;
	
	public AbstractApplicationContext() {
		this(null);
	}
	
	public AbstractApplicationContext(String file) {
		beanReader = getBeanReader(file);
		addObjectToContext();
	}
	
	private void addObjectToContext() {
		for (BeanDefinition beanDefinition : beanReader.loadBeanDefinitions()) {
			inject(beanDefinition);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(Class<T> clazz) {
		return (T) map.get(clazz.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(String id) {
		return (T) map.get(id);
	}
	
	protected void addObjectToContext(BeanDefinition beanDefinition) {
		// 增加声明的组件到 ApplicationContext
		Object object = beanDefinition.getObject();
		// 把id作为key
		String id = beanDefinition.getId();
		if (VerifyUtils.isNotEmpty(id))
			map.put(id, object);

		// 把类名作为key
		map.put(beanDefinition.getClassName(), object);

		// 把接口名作为key
		Set<String> keys = beanDefinition.getInterfaceNames();
		for (String k : keys) {
			map.put(k, object);
		}
	}
	
	abstract protected BeanReader getBeanReader(String file);
	
	abstract protected Object inject(BeanDefinition beanDef);

}
