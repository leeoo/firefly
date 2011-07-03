package com.firefly.template;

import java.lang.reflect.Method;

public interface ClassCache {
	Method get(Class<?> clazz, String propertyName);
	void put(Class<?> clazz, String propertyName, Method method);
}
