package com.firefly.mvc.web.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 保存请求key对应的对象
 * @author alvinqiu
 *
 */
public class BeanHandle {
	private final Object object;
	private final Method method;


	public BeanHandle(Object object, Method method) {
		super();
		this.object = object;
		this.method = method;
	}

	public Object getObject() {
		return object;
	}


	public Method getMethod() {
		return method;
	}

	public Object invoke(Object... obj) {
		Object ret = null;
		try {
			ret = method.invoke(object, obj);
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
