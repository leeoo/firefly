package com.firefly.core.support;

import java.util.Properties;
import java.util.Set;

public interface BeanReader {
	Set<Class<?>> getClasses();

	Properties getProperties();

	String DEFAULT_CONFIG_FILE = "firefly.properties";
}
