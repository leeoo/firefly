package com.firefly.utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ReflectUtils {
	
	public static String getPropertyNameBySetterMethod(Method method) {
		String methodName = method.getName();
		String propertyName = Character.toLowerCase(methodName.charAt(3))
				+ methodName.substring(4);
		return propertyName;
	}
	
	public static Map<String, Method> getSetterMethods(Class<?> paraType) {
		Map<String, Method> beanSetMethod = new HashMap<String, Method>();
		Method[] methods = paraType.getMethods();

		for (Method method : methods) {

			if (!method.getName().startsWith("set")
					|| Modifier.isStatic(method.getModifiers())
					|| !method.getReturnType().equals(Void.TYPE)
					|| method.getParameterTypes().length != 1) {
				continue;
			}
			String propertyName = getPropertyNameBySetterMethod(method);
			method.setAccessible(true);
			beanSetMethod.put(propertyName, method);
		}
		return beanSetMethod;
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
