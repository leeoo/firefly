package com.firefly.core.support;

import java.util.Properties;
import java.util.Set;

public interface BeanReader {
	Set<Class<?>> getClasses();

	/**
	 * 读取一个包下面所有声明组件的类型
	 *
	 * @param pack
	 */
	void load(String file);

	void load();

	Properties getProperties();

	String DEFAULT_CONFIG_FILE = "firefly.properties";
}
