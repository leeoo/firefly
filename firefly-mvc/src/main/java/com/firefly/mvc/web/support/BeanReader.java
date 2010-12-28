package com.firefly.mvc.web.support;

import java.util.Set;

public interface BeanReader {
	Set<Class<?>> getClasses();

	/**
	 * 读取一个包下面所有声明组件的类型
	 * @param pack
	 */
	void load(String pack);
}
