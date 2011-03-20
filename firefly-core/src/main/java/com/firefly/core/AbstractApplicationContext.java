package com.firefly.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.firefly.core.support.BeanDefinition;
import com.firefly.utils.VerifyUtils;

abstract public class AbstractApplicationContext implements ApplicationContext {

	protected Map<String, Object> map = new HashMap<String, Object>();
	protected List<BeanDefinition> beanDefinitions;
	
	public AbstractApplicationContext() {
		this(null);
	}
	
	public AbstractApplicationContext(String file) {
		beanDefinitions = getBeanDefinitions(file);
		addObjectToContext();
	}
	
	private void addObjectToContext() {
		for (BeanDefinition beanDefinition : beanDefinitions) {
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
	
	protected BeanDefinition findBeanDefinition(String key) {
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
	
	abstract protected List<BeanDefinition> getBeanDefinitions(String file);
	
	abstract protected Object inject(BeanDefinition beanDef);

}
