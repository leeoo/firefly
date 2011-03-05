package com.firefly.mvc.web.support;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.firefly.annotation.Component;
import com.firefly.annotation.Controller;
import com.firefly.annotation.Interceptor;
import com.firefly.annotation.RequestMapping;
import com.firefly.core.support.annotation.AnnotationBeanReader;

public class WebBeanReader extends AnnotationBeanReader {

	public WebBeanReader() {
		this(null);
	}

	public WebBeanReader(String file) {
		super(file);
	}

	@Override
	protected boolean isComponent(Class<?> c) {
		return c.isAnnotationPresent(Controller.class)
				|| c.isAnnotationPresent(Interceptor.class)
				|| c.isAnnotationPresent(Component.class);
	}

	@Override
	protected void addBeanDefinition(Class<?> c) {
		WebBeanDefinition webBeanDefinition = new WebAnnotatedBeanDefinition();
		webBeanDefinition.setClassName(c.getName());

		String id = getId(c);
		webBeanDefinition.setId(id);

		Set<String> names = getInterfaceNames(c);
		webBeanDefinition.setInterfaceNames(names);

		List<Field> fields = getInjectField(c);
		webBeanDefinition.setInjectFields(fields);

		List<Method> methods = getInjectMethod(c);
		webBeanDefinition.setInjectMethods(methods);

		try {
			Object object = c.newInstance();
			webBeanDefinition.setObject(object);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		List<Method> reqMethods = getReqMethods(c);
		webBeanDefinition.setReqMethods(reqMethods);

		List<Method> interceptorMethods = getInterceptors(c);
		webBeanDefinition.setInterceptorMethods(interceptorMethods);

		if (c.isAnnotationPresent(Interceptor.class)) {
			String uriPattern = c.getAnnotation(Interceptor.class).uri();
			webBeanDefinition.setUriPattern(uriPattern);

			String view = c.getAnnotation(Interceptor.class).view();
			webBeanDefinition.setView(view);

			Integer order = c.getAnnotation(Interceptor.class).order();
			webBeanDefinition.setOrder(order);
		}

		beanDefinitions.add(webBeanDefinition);
	}

	protected String getId(Class<?> c) {
		if (c.isAnnotationPresent(Controller.class))
			return c.getAnnotation(Controller.class).value();
		else if (c.isAnnotationPresent(Interceptor.class))
			return c.getAnnotation(Interceptor.class).value();
		else if (c.isAnnotationPresent(Component.class))
			return c.getAnnotation(Component.class).value();
		else
			return "";
	}

	protected List<Method> getReqMethods(Class<?> c) {
		Method[] methods = c.getMethods();
		List<Method> list = new ArrayList<Method>();
		if (c.isAnnotationPresent(Controller.class)
				|| c.isAnnotationPresent(Component.class)) {
			for (Method m : methods) {
				if (m.isAnnotationPresent(RequestMapping.class)) {
					list.add(m);
				}
			}
		}
		return list;
	}

	protected List<Method> getInterceptors(Class<?> c) {
		Method[] methods = c.getMethods();
		List<Method> list = new ArrayList<Method>();
		if (c.isAnnotationPresent(Interceptor.class)
				|| c.isAnnotationPresent(Component.class)) {
			for (Method m : methods) {// 验证方法名
				if (m.getName().equals("before") || m.getName().equals("after")) {
					list.add(m);
				}
			}
		}
		return list;
	}
}
