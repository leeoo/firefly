package com.firefly.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import com.firefly.annotation.Inject;
import com.firefly.core.support.annotation.AnnotationBeanDefinition;
import com.firefly.core.support.annotation.AnnotationBeanReader;
import com.firefly.utils.VerifyUtils;

public class AnnotationApplicationContext extends AbstractApplicationContext {

	protected List<AnnotationBeanDefinition> beanDefinitions;

	public AnnotationApplicationContext() {
		this(null);
	}

	public AnnotationApplicationContext(String file) {
		beanDefinitions = getBeanReader(file);
		addObjToContext();
		inject();
	}

	protected List<AnnotationBeanDefinition> getBeanReader(String file) {
		return new AnnotationBeanReader(file).loadBeanDefinitions();
	}

	private void addObjToContext() {
		for (AnnotationBeanDefinition beanDefinition : beanDefinitions) {
			// 增加声明的组件到 ApplicationContext
			Object object = beanDefinition.getObject();
			String id = beanDefinition.getId();
			if (VerifyUtils.isNotEmpty(id))
				map.put(id, object);
			map.put(beanDefinition.getClassName(), object);

			Set<String> keys = beanDefinition.getInterfaceNames();
			for (String k : keys) {
				map.put(k, object);
			}
		}
	}

	private void inject() {
		for (AnnotationBeanDefinition beanDefinition : beanDefinitions) {
			Object object = beanDefinition.getObject();

			// 属性注入
			for (Field field : beanDefinition.getInjectFields()) {
				field.setAccessible(true);
				Class<?> clazz = field.getType();
				String key = field.getAnnotation(Inject.class).value();
				Object instance = map.get(key.length() > 0 ? key : clazz
						.getName());
				if (instance != null) {
					try {
						field.set(object, instance);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}

			// 从方法注入
			for (Method method : beanDefinition.getInjectMethods()) {
				method.setAccessible(true);
				Class<?>[] params = method.getParameterTypes();
				Object[] p = new Object[params.length];
				for (int i = 0; i < p.length; i++) {
					Object instance = map.get(params[i].getName());
					if (instance != null) {
						p[i] = instance;
					}
				}
				try {
					method.invoke(object, p);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
