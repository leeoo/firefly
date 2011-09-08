package com.firefly.utils.json.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static com.firefly.utils.json.JsonStringSymbol.QUOTE;
import static com.firefly.utils.json.JsonStringSymbol.OBJ_SEPARATOR;

public class JsonObjMetaInfo {
	private char[] propertyName;
	private Method method;

	public char[] getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = (QUOTE + propertyName + QUOTE + OBJ_SEPARATOR).toCharArray();
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object invoke(Object obj) {
		Object ret = null;
		try {
			ret = method.invoke(obj);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return ret;
	}

}
