package com.firefly.core;

import java.util.HashMap;
import java.util.Map;

abstract public class AbstractApplicationContext implements ApplicationContext {

	protected Map<String, Object> map = new HashMap<String, Object>();

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
