package com.firefly.mvc.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.firefly.annotation.RequestMapping;
import com.firefly.core.AbstractApplicationContext;
import com.firefly.mvc.web.support.BeanHandle;
import com.firefly.mvc.web.support.ViewHandle;
import com.firefly.mvc.web.support.view.JspViewHandle;
import com.firefly.mvc.web.support.view.RedirectHandle;
import com.firefly.mvc.web.support.view.TextViewHandle;

/**
 * Web应用上下文默认实现
 *
 * @author AlvinQiu
 *
 */
public class DefaultWebContext extends AbstractApplicationContext implements
		WebContext {
	private static Logger log = LoggerFactory
			.getLogger(DefaultWebContext.class);

	private interface Config {
		String VIEW_PATH = "viewPath";
		String DEFAULT_VIEW_PATH = "/WEB-INF/page";
		String ENCODING = "encoding";
		String DEFAULT_ENCODING = "UTF-8";
	}

	private DefaultWebContext() {
	}

	private static class DefaultWebContextHolder {
		private static DefaultWebContext instance = new DefaultWebContext();
	}

	public static DefaultWebContext getInstance() {
		return DefaultWebContextHolder.instance;
	}

	@Override
	public WebContext load() {
		super.load();
		return this;
	}

	@Override
	public WebContext load(String file) {
		super.load(file);
		return this;
	}

	@Override
	public String getEncoding() {
		return beanReader.getProperties().getProperty(Config.ENCODING,
				Config.DEFAULT_ENCODING);
	}

	@Override
	public Object getBean(String id) {
		return map.get(id);
	}

	@Override
	public String getViewPath() {
		return beanReader.getProperties().getProperty(Config.VIEW_PATH,
				Config.DEFAULT_VIEW_PATH);
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

	@Override
	public void addObjectToContext(Class<?> c, Object o) {
		// 注册Controller里面声明的uri
		List<Method> list = getReqMethod(c.getMethods());
		for (Method m : list) {
			m.setAccessible(true);
			final String url = m.getAnnotation(RequestMapping.class).value();
			final String method = m.getAnnotation(RequestMapping.class)
					.method();
			String view = m.getAnnotation(RequestMapping.class).view();
			String key = method + "@" + url;

			ViewHandle viewHandle = null;
			if (view.equals(View.JSP)) {
				viewHandle = JspViewHandle.getInstance().init(getViewPath());
			}
			if (view.equals(View.TEXT)) {
				viewHandle = TextViewHandle.getInstance().init(getEncoding());
			}
			if (view.equals(View.REDIRECT)) {
				viewHandle = RedirectHandle.getInstance();
			}

			BeanHandle beanHandle = new BeanHandle(o, m, viewHandle);
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
