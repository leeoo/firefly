package com.firefly.core.support;

import java.util.Map;


/**
 * 加载配置文件接口
 * @author 杰然不同
 * @date 2010-11-28
 * @Version 1.0
 */
public interface BeanDefinitionReader {

	/**
	 * 读取配置文件中的信息
	 * @Date 2010-11-29
	 * @param fileName 文件名
	 * @return map
	 */
	public abstract Map<String, BeanDefinition> loadBeanDefinitions();
	
}
