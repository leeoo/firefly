package com.firefly.mvc.web;

import java.io.IOException;
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

			for (Class<?> c : classes) {
				Object o = c.newInstance();
				List<Method> list = hasReqMethod(c.getMethods());
				add(c, o);

				for (Method m : list) {
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
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	private List<Method> hasReqMethod(Method[] methods) {
		List<Method> list = new ArrayList<Method>();
		for (Method m : methods) {
			if (m.isAnnotationPresent(RequestMapping.class)) {
				list.add(m);
			}
		}
		return list;
	}

	private void add(Class<?> c, Object obj) {
		Set<String> keys = getInstanceMapKeys(c);
		for (String k : keys) {
			log.info("obj key [{}]", k);
			map.put(k, obj);
		}
	}

	private Set<String> getInstanceMapKeys(Class<?> c) {
		Set<String> ret = new LinkedHashSet<String>();
		ret.add(c.getName());

		Class<?>[] interfaces = c.getInterfaces();
		for (Class<?> i : interfaces) {
			ret.add(i.getName());
		}

		Controller controller = c.getAnnotation(Controller.class);
		if (controller != null && controller.value().length() > 0)
			ret.add(controller.value());
		Component component = c.getAnnotation(Component.class);
		if (component != null && component.value().length() > 0)
			ret.add(component.value());
		return ret;
	}

}
