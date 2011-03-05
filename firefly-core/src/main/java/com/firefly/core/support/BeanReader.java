package com.firefly.core.support;

import java.util.List;

public interface BeanReader {
	List<? extends BeanDefinition> loadBeanDefinitions();
}
