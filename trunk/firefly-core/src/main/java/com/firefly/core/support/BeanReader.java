package com.firefly.core.support;

import java.util.List;

import com.firefly.utils.Pair;

public interface BeanReader {
	List<Pair<Class<?>, Object>> getClasses();
}
