package com.firefly.mvc.web.impl;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.RequestMapping;
import com.firefly.core.ApplicationContext;
import com.firefly.mvc.web.FontController;
import com.firefly.utils.PackageScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 前端控制器
 *
 * @author alvinqiu
 *
 */
public class FrontControllerImpl implements FontController {
	private static Logger log = LoggerFactory
			.getLogger(FrontControllerImpl.class);
	private Properties prop;
	private Map<String, BeanHandle> uriMap;
	private String[] componentPath;

	private interface Config {
		String VIEW_PATH = "viewPath";
		String DEFAULT_VIEW_PATH = "/WEB-INF/page";
		String COMPONENT_PATH = "componentPath";
	}

	private FrontControllerImpl() {

	}

	private static class FrontProcessorHolder {
		private static FontController instance = new FrontControllerImpl();
	}

	public static FontController getInstance() {
		return FrontProcessorHolder.instance;
	}

	public void dispatcher(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String uri = request.getRequestURI();
		String prePath = request.getContextPath() + request.getServletPath();
		String viewPath = getInstance().getViewPath();
		String invokeUri = uri.substring(prePath.length());
		String key = request.getMethod().toUpperCase() + "@" + invokeUri;

		log.info("uri map [{}]", key);
		BeanHandle beanHandle = uriMap.get(key);

		// TODO 此处还需要完善 1)增加请求参数封装到javabean, 2)反射调用方法参数柔性处理, 3)json返回的处理
		Object o = beanHandle.invoke(request);
		if (o instanceof String) {
			request.getRequestDispatcher(viewPath + o.toString()).forward(
					request, response);
		}
	}

	public void load(String file) {
		try {
			prop = new Properties();
			uriMap = new HashMap<String, BeanHandle>();

			prop
					.load(FrontControllerImpl.class.getResourceAsStream("/"
							+ file));
			componentPath = prop.getProperty(Config.COMPONENT_PATH).split(",");
			final Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
			for (String pack : componentPath) {
				log.info("componentPath [{}]", pack);
				PackageScan.getClasses(pack.trim(), classes);
			}

			for (Class<?> c : classes) {
				try {
					Object o = c.newInstance();
					List<Method> list = hasReqMethod(c.getMethods());

					ApplicationContext.getInstance().add(c, o);

					for (Method m : list) {
						final String url = m
								.getAnnotation(RequestMapping.class).value();
						final String method = m.getAnnotation(
								RequestMapping.class).method();
						String key = method + "@" + url;

						BeanHandle beanHandle = new BeanHandle(o, m);
						uriMap.put(key, beanHandle);
						log.info("uri map [{}]", key);
						if (key.charAt(key.length() - 1) == '/')
							key = key.substring(0, key.length() - 1);
						else
							key = key + "/";
						uriMap.put(key, beanHandle);
						log.info("uri map [{}]", key);
					}

				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getViewPath() {
		return prop.getProperty(Config.VIEW_PATH, Config.DEFAULT_VIEW_PATH);
	}

	public String[] getComponentPath() {
		return componentPath;
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

}
