package com.firefly.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.firefly.annotation.Inject;
import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.BeanReader;
import com.firefly.core.support.annotation.AnnotationBeanDefinition;
import com.firefly.core.support.annotation.AnnotationBeanReader;

public class AnnotationApplicationContext extends AbstractApplicationContext {

	public AnnotationApplicationContext() {
		this(null);
	}

	public AnnotationApplicationContext(String file) {
		super(file);
	}

	@Override
	protected BeanReader getBeanReader(String file) {
		return new AnnotationBeanReader(file);
	}

	@Override
	protected Object inject(BeanDefinition beanDef) {
		AnnotationBeanDefinition beanDefinition = (AnnotationBeanDefinition) beanDef;
		fieldInject(beanDefinition);
		methodInject(beanDefinition);
		addObjectToContext(beanDefinition);
		return beanDefinition.getObject();
	}
	
	private void fieldInject(AnnotationBeanDefinition beanDefinition) {
		Object object = beanDefinition.getObject();

		// 属性注入
		for (Field field : beanDefinition.getInjectFields()) {
			field.setAccessible(true);
			Class<?> clazz = field.getType();
			String key = field.getAnnotation(Inject.class).value();
			Object instance = map.get(key.length() > 0 ? key : clazz.getName());
			if (instance == null) {
				BeanDefinition b = beanReader.findBeanDefinition(key);
				if (b != null)
					instance = inject(b);
			}
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
	}
	
	private void methodInject(AnnotationBeanDefinition beanDefinition) {
		Object object = beanDefinition.getObject();
		
		// 从方法注入
		for (Method method : beanDefinition.getInjectMethods()) {
			method.setAccessible(true);
			Class<?>[] params = method.getParameterTypes();
			Object[] p = new Object[params.length];
			for (int i = 0; i < p.length; i++) {
				String key = params[i].getName();
				Object instance = map.get(key);
				if (instance != null) {
					p[i] = instance;
				} else {
					BeanDefinition b = beanReader.findBeanDefinition(key);
					if (b != null)
						p[i] = inject(b);
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
