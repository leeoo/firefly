package com.firefly.mvc.web.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import com.firefly.utils.ConvertUtils;

public class ParamMetaInfo {
	private final Class<?> paramClass;
	private final Map<String, Method> beanSetMethod;
	private final String attribute;

	public ParamMetaInfo(Class<?> paramClass, Map<String, Method> beanSetMethod,
			String attribute) {
		super();
		this.paramClass = paramClass;
		this.beanSetMethod = beanSetMethod;
		this.attribute = attribute;
	}

	public String getAttribute() {
		return attribute;
	}

	/**
	 * 给参数对象的实例赋值
	 * @param o 要赋值的对象
	 * @param key 要赋值的属性
	 * @param value 要赋的值
	 */
	public void setParam(Object o, String key, String value) {
		try {
			Method m = beanSetMethod.get(key);
			if (m != null) {
				Class<?> p = m.getParameterTypes()[0];
				m.invoke(o, ConvertUtils.convert(value, p));
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 新建一个参数对象实例
	 * @return
	 */
	public Object newParamInstance() {
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
