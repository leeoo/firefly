package com.firefly.mvc.web.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import com.firefly.utils.Cast;

public class ParamHandle {
	private Class<?> paramClass;
	private Map<String, Method> beanGetAndSetMethod;
	private String attribute;

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public Map<String, Method> getBeanGetAndSetMethod() {
		return beanGetAndSetMethod;
	}

	public void setBeanGetAndSetMethod(Map<String, Method> beanGetAndSetMethod) {
		this.beanGetAndSetMethod = beanGetAndSetMethod;
	}

	public Class<?> getParamClass() {
		return paramClass;
	}

	public void setParamClass(Class<?> paramClass) {
		this.paramClass = paramClass;
	}

	public void setParam(Object o, String key, String value) {
		try {
			Method m = beanGetAndSetMethod.get(key);
			if (m != null) {
				Class<?> p = m.getParameterTypes()[0];
				m.invoke(o, Cast.convert(value, p));
			}
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
