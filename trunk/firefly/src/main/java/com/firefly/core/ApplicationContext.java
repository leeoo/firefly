package com.firefly.core;

/**
 * IOC容器基本接口
 * @author 杰然不同
 * @date 2010-11-26
 * @Description: 定义IOC容器基本规范
 * @Version 1.0
 */
public interface ApplicationContext {
	public Object getBean(String name);
}
