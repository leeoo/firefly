package com.firefly.core;

import com.firefly.core.support.xml.XmlBeanDefinitionReader;

/**
 * 容器接口实现
 * @author 杰然不同
 * @date 2010-11-29
 * @Version 1.0
 */
public class FileSystemXmlApplicationContext extends AbstractApplicationContext{
	
	public FileSystemXmlApplicationContext(String fileName) {
		super.reader = new XmlBeanDefinitionReader(fileName);
		
		// 启动容器初始化
		refresh();
	}
}
