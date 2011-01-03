package com.firefly.core;

public interface ApplicationContext {
	Object getBean(String id);

	<T> T getBean(Class<T> clazz);
}
