package com.firefly.mvc.web.support;

import java.util.Set;

public interface BeanReader {
	Set<Class<?>> getClasses();
	void load(String pack);
}
