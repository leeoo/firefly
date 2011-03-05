package com.firefly.core;

import java.util.Map;

abstract public class AbstractApplicationContext implements ApplicationContext {

	protected Map<String, Object> map;

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

}
