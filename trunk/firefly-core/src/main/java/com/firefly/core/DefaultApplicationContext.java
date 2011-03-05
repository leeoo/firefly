package com.firefly.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.firefly.annotation.Component;
import com.firefly.annotation.Controller;
import com.firefly.annotation.Inject;
import com.firefly.annotation.Interceptor;
import com.firefly.core.support.annotation.AnnotationBeanReader;
import com.firefly.utils.Pair;

public class DefaultApplicationContext extends AbstractApplicationContext {

	protected List<Pair<Class<?>, Object>> components;

	public DefaultApplicationContext() {
		this(null);
	}

	public DefaultApplicationContext(String file) {
		try {
			components = new ArrayList<Pair<Class<?>, Object>>();
			map = new HashMap<String, Object>();
			addObjToContext(new AnnotationBeanReader(file).getClasses());
			inject();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private void addObjToContext(Set<Class<?>> classes) throws InstantiationException, IllegalAccessException {
		for (Class<?> c : classes) {
			Object o = c.newInstance();
			components.add(new Pair<Class<?>, Object>(c, o));
			// 增加声明的组件到 ApplicationContext
			Set<String> keys = getInstanceMapKeys(c);
			for (String k : keys) {
				// log.info("obj key [{}]", k);
				map.put(k, o);
			}
		}
	}

	private void inject() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		for (Pair<Class<?>, Object> pair : components) {
			Class<?> clz = pair.obj1;
			Object o = pair.obj2;

			// 属性注入
			Field[] fields = clz.getDeclaredFields();
			List<Field> fieldList = getInjectField(fields);
			for (Field field : fieldList) {
				field.setAccessible(true);
				Class<?> clazz = field.getType();
				String key = field.getAnnotation(Inject.class).value();
				Object instance = map.get(key.length() > 0 ? key : clazz
						.getName());
				if (instance != null)
					field.set(o, instance);
			}

			// 从方法注入
			Method[] methods = clz.getDeclaredMethods();
			List<Method> methodList = getInjectMethod(methods);
			for (Method method : methodList) {
				method.setAccessible(true);
				Class<?>[] params = method.getParameterTypes();
				Object[] p = new Object[params.length];
				for (int i = 0; i < p.length; i++) {
					Object instance = map.get(params[i].getName());
					if (instance != null) {
						p[i] = instance;
					}
				}
				method.invoke(o, p);
			}
		}
	}

	private List<Method> getInjectMethod(Method[] methods) {
		List<Method> list = new ArrayList<Method>();
		for (Method m : methods) {
			if (m.isAnnotationPresent(Inject.class)) {
				list.add(m);
			}
		}
		return list;
	}

	/**
	 * 找出组件的域里面包含Inject注释的域
	 *
	 * @param fields
	 * @return
	 */
	private List<Field> getInjectField(Field[] fields) {
		List<Field> list = new ArrayList<Field>();
		for (Field field : fields) {
			if (field.getAnnotation(Inject.class) != null) {
				list.add(field);
			}
		}
		return list;
	}

	/**
	 * 获取声明组件的所有访问key
	 *
	 * @param c
	 * @return
	 */
	private Set<String> getInstanceMapKeys(Class<?> c) {
		Set<String> ret = new LinkedHashSet<String>();
		// 直接把类名作为key
		ret.add(c.getName());

		// 把该类实现的接口名作为key
		Class<?>[] interfaces = c.getInterfaces();
		for (Class<?> i : interfaces) {
			ret.add(i.getName());
		}

		// 把annotation的值作为key
		Controller controller = c.getAnnotation(Controller.class);
		if (controller != null && controller.value().length() > 0)
			ret.add(controller.value());
		Component component = c.getAnnotation(Component.class);
		if (component != null && component.value().length() > 0)
			ret.add(component.value());
		Interceptor interceptor = c.getAnnotation(Interceptor.class);
		if (interceptor != null && interceptor.value().length() > 0)
			ret.add(interceptor.value());
		return ret;
	}

}
