package com.firefly.mvc.web;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.annotation.Component;
import com.firefly.annotation.Controller;
import com.firefly.annotation.Inject;
import com.firefly.annotation.RequestMapping;
import com.firefly.mvc.web.support.BeanHandle;
import com.firefly.mvc.web.support.BeanReader;
import com.firefly.mvc.web.support.annotation.AnnotationBeanReader;

/**
 * Web应用上下文默认实现
 *
 * @author AlvinQiu
 *
 */
public class DefaultWebContext implements WebContext {
	private static Logger log = LoggerFactory
			.getLogger(DefaultWebContext.class);

	private Properties prop;
	private Map<String, Object> map;
	private List<Object> list;
	private final BeanReader beanReader;

	private interface Config {
		String VIEW_PATH = "viewPath";
		String DEFAULT_VIEW_PATH = "/WEB-INF/page";
		String COMPONENT_PATH = "componentPath";
		String ENCODING = "encoding";
		String DEFAULT_ENCODING = "UTF-8";
	}

	private DefaultWebContext() {
		beanReader = AnnotationBeanReader.getInstance();
	}

	private static class PropertiesWebContextHolder {
		private static DefaultWebContext instance = new DefaultWebContext();
	}

	public static DefaultWebContext getInstance() {
		return PropertiesWebContextHolder.instance;
	}

	@Override
	public String getEncoding() {
		return prop.getProperty(Config.ENCODING, Config.DEFAULT_ENCODING);
	}

	@Override
	public Object getBean(String id) {
		return map.get(id);
	}

	@Override
	public String getViewPath() {
		return prop.getProperty(Config.VIEW_PATH, Config.DEFAULT_VIEW_PATH);
	}

	@Override
	public void load(String file) {
		try {
			prop = new Properties();
			map = new HashMap<String, Object>();

			prop.load(DefaultWebContext.class.getResourceAsStream("/" + file));
			final String[] componentPath = prop.getProperty(
					Config.COMPONENT_PATH).split(",");

			for (String pack : componentPath) {
				log.info("componentPath [{}]", pack);
				beanReader.load(pack.trim());
			}
			final Set<Class<?>> classes = beanReader.getClasses();
			init(classes);
			inject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 把对象都加入到WebContext里面
	 *
	 * @param classes
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private void init(Set<Class<?>> classes) throws InstantiationException,
			IllegalAccessException {
		list = new ArrayList<Object>();
		for (Class<?> c : classes) {
			Object o = c.newInstance();
			list.add(o);

			// 增加声明的组件到WebContext
			Set<String> keys = getInstanceMapKeys(c);
			for (String k : keys) {
				log.info("obj key [{}]", k);
				map.put(k, o);
			}

			// 增加Controller的uri绑定bean的Method映射到WebContext
			List<Method> list = getReqMethod(c.getMethods());
			for (Method m : list) {
				m.setAccessible(true);
				final String url = m.getAnnotation(RequestMapping.class)
						.value();
				final String method = m.getAnnotation(RequestMapping.class)
						.method();
				String view = m.getAnnotation(RequestMapping.class).view();
				String key = method + "@" + url;

				BeanHandle beanHandle = new BeanHandle(o, m, view);
				map.put(key, beanHandle);
				log.info("uri map [{}]", key);
				if (key.charAt(key.length() - 1) == '/')
					key = key.substring(0, key.length() - 1);
				else
					key = key + "/";
				map.put(key, beanHandle);
				log.info("uri map [{}]", key);
			}
		}
	}

	/**
	 * 把WebContext里面的对象构造注入
	 *
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void inject() throws IllegalArgumentException,
			IllegalAccessException {
		log.info("================into inject===============");
		for (Object o : list) {
			Field[] fields = o.getClass().getDeclaredFields();
			List<Field> list = getInjectField(fields);
			log.info("[{}] has inject field size [{}]", o.getClass().getName(),
					list.size());
			for (Field field : list) {
				Class<?> clazz = field.getType();
				Object instance = map.get(clazz.getName());
				log.info("obj [{}] inject instance [{}]", clazz.getName(),
						instance.getClass().getName());
				field.setAccessible(true);
				field.set(o, instance);
			}
		}
		log.info("================end inject===============");
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
	 * 找出Controller里面标记有RequestMapping的方法
	 */
	private List<Method> getReqMethod(Method[] methods) {
		List<Method> list = new ArrayList<Method>();
		for (Method m : methods) {
			if (m.isAnnotationPresent(RequestMapping.class)) {
				list.add(m);
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
		return ret;
	}

}