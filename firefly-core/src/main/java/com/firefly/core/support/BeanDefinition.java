package com.firefly.core.support;

import java.util.HashMap;
import java.util.Map;

/**
 * Bean信息
 * @author 杰然不同
 * @date 2010-11-29
 * @Version 1.0
 */
public class BeanDefinition {
	
	// Bean的id
	private String id;
	
	// Bean的class
	private String className;
	
	// Bean的属性集合
	private Map<String, Object> properties = new HashMap<String, Object>();
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
}
