package com.firefly.mvc.web.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import com.firefly.utils.Cast;

public class ParamHandle {
	private Class<?> paramClass;
	private Map<String, Method> map;
	private String attribute;

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public Map<String, Method> getMap() {
		return map;
	}

	public void setMap(Map<String, Method> map) {
		this.map = map;
	}

	public Class<?> getParamClass() {
		return paramClass;
	}

	public void setParamClass(Class<?> paramClass) {
		this.paramClass = paramClass;
	}

	public void setParam(Object o, String key, String value) {
		try {
			Method m = map.get(key);
			Class<?> p = m.getParameterTypes()[0];
			m.invoke(o, Cast.convert(value, p));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	public Object newInstance() {
		Object o = null;
		try {
			o = paramClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return o;
	}

}
