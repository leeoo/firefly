package com.firefly.mvc.web.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 保存请求key对应的对象
 *
 * @author alvinqiu
 *
 */
public class BeanHandle {
	private final Object object;
	private final Method method;
	private final String view;
	private final Class<?>[] paraTypes;
	private final String[] paraClassNames;

	public BeanHandle(Object object, Method method, String view) {
		super();
		this.object = object;
		this.method = method;
		this.view = view;
		paraTypes = method.getParameterTypes();
		paraClassNames = new String[paraTypes.length];
		for (int i = 0; i < paraTypes.length; i++) {
			paraClassNames[i] = paraTypes[i].getName();
		}
	}

	public String[] getParaClassNames() {
		return paraClassNames;
	}

	public Object getObject() {
		return object;
	}

	public Method getMethod() {
		return method;
	}

	public String getView() {
		return view;
	}

	public Object invoke(Object[] args) {
		Object ret = null;
		try {
			ret = method.invoke(object, args);
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
