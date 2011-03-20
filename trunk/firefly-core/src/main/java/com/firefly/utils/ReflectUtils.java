package com.firefly.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public abstract class ReflectUtils {
	public static Method[] getSetterMethods(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		List<Method> list = new ArrayList<Method>();
		for (Method method : methods) {
			method.setAccessible(true);
			String methodName = method.getName();
			if (!methodName.startsWith("set")
					|| Modifier.isStatic(method.getModifiers())
					|| !method.getReturnType().equals(Void.TYPE)
					|| method.getParameterTypes().length != 1) {
				continue;
			}
			list.add(method);
		}
		return list.toArray(new Method[0]);
	}
	
	/**
	 * 获取所有接口名称
	 * @param c
	 * @return
	 */
	public static String[] getInterfaceNames(Class<?> c) {
		Class<?>[] interfaces = c.getInterfaces();
		List<String> names = new ArrayList<String>();
		for (Class<?> i : interfaces) {
			names.add(i.getName());
		}
		return names.toArray(new String[0]);
	}
}
