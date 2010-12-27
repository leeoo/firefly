package com.firefly.core;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.firefly.annotation.Component;
import com.firefly.annotation.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 应用程序上下文
 * @author alvinqiu
 *
 */
public class ApplicationContext {
	private static Logger log = LoggerFactory
			.getLogger(ApplicationContext.class);
	private Map<String, Object> instanceMap;

	private ApplicationContext() {
		instanceMap = new HashMap<String, Object>();
	}

	private static class ApplicationContextHolder {
		private static ApplicationContext instance = new ApplicationContext();
	}

	public static ApplicationContext getInstance() {
		return ApplicationContextHolder.instance;
	}

	public Object getBean(String id) {
		return instanceMap.get(id);
	}

	public void add(Class<?> c, Object obj) {
		Set<String> keys = getInstanceMapKeys(c);
		for (String k : keys) {
			log.info("instance key [{}]", k);
			instanceMap.put(k, obj);
		}
	}

	private Set<String> getInstanceMapKeys(Class<?> c) {
		Set<String> ret = new LinkedHashSet<String>();
		ret.add(c.getName());
		// log.info("add instance key [{}]", c.getName());

		Class<?>[] interfaces = c.getInterfaces();
		for (Class<?> i : interfaces) {
			// log.info("add instance key [{}]", i.getName());
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
