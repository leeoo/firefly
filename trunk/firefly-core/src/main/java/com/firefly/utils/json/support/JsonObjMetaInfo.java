package com.firefly.utils.json.support;

import java.lang.reflect.Method;

public class JsonObjMetaInfo {
	private String propertyName;
	private Method method;

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

}
