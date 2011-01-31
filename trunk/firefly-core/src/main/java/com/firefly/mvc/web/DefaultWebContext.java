package com.firefly.mvc.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.annotation.Interceptor;
import com.firefly.annotation.RequestMapping;
import com.firefly.core.AbstractApplicationContext;
import com.firefly.mvc.web.support.BeanHandle;
import com.firefly.mvc.web.support.ViewHandle;
import com.firefly.mvc.web.support.view.JsonViewHandle;
import com.firefly.mvc.web.support.view.JspViewHandle;
import com.firefly.mvc.web.support.view.RedirectHandle;
import com.firefly.mvc.web.support.view.TextViewHandle;
import com.firefly.utils.StringUtils;
import com.firefly.utils.VerifyUtils;

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
	private List<String> uriList = new ArrayList<String>();

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
		return load(null);
	}

	@Override
	public WebContext load(String file) {
		super.load(file);
		JspViewHandle.getInstance().init(getViewPath());
		TextViewHandle.getInstance().init(getEncoding());
		JsonViewHandle.getInstance().init(getEncoding());
		return this;
	}

	@Override
	public String getEncoding() {
		return beanReader.getProperties().getProperty(Config.ENCODING,
				Config.DEFAULT_ENCODING);
	}

	@Override
	public String getViewPath() {
		return beanReader.getProperties().getProperty(Config.VIEW_PATH,
				Config.DEFAULT_VIEW_PATH);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addObjectToContext(Class<?> c, Object o) {
		// 注册Controller里面声明的uri
		List<Method> list = getReqMethod(c);
		for (Method m : list) {
			m.setAccessible(true);
			final String uri = m.getAnnotation(RequestMapping.class).value();
			final String method = m.getAnnotation(RequestMapping.class)
					.method();
			final String view = m.getAnnotation(RequestMapping.class).view();
			String key = method + "@" + uri;

			BeanHandle beanHandle = new BeanHandle(o, m, getViewHandle(view));
			map.put(key, beanHandle);
			uriList.add(key);
			log.info("register uri [{}]", key);
			if (key.charAt(key.length() - 1) == '/')
				key = key.substring(0, key.length() - 1);
			else
				key += "/";
			map.put(key, beanHandle);
			uriList.add(key);
			log.info("register uri [{}]", key);
		}

		list = getInterceptor(c);
		for (Method m : list) {
			m.setAccessible(true);
			String uriPattern = c.getAnnotation(Interceptor.class).uri();
			final String view = c.getAnnotation(Interceptor.class).view();
			final Integer order = c.getAnnotation(Interceptor.class).order();

			List<String> l = getInterceptUri(uriPattern);
			for (String i : l) {
				String key = m.getName().charAt(0) + "#" + i;
				BeanHandle beanHandle = new BeanHandle(o, m,
						getViewHandle(view));
				beanHandle.setInterceptOrder(order);
				Set<BeanHandle> interceptorSet = (Set<BeanHandle>) map.get(key);
				if (interceptorSet == null) {
					interceptorSet = new TreeSet<BeanHandle>();
					interceptorSet.add(beanHandle);
					map.put(key, interceptorSet);
				} else {
					interceptorSet.add(beanHandle);
				}
			}
		}

	}

	/**
	 * 找出Controller里面标记有RequestMapping的方法
	 */
	private List<Method> getReqMethod(Class<?> c) {
		Method[] methods = c.getMethods();
		List<Method> list = new ArrayList<Method>();
		for (Method m : methods) {
			if (m.isAnnotationPresent(RequestMapping.class)) {
				list.add(m);
			}
		}
		return list;
	}

	/**
	 * 获取所有拦截器
	 *
	 * @param c
	 * @return
	 */
	private List<Method> getInterceptor(Class<?> c) {
		Method[] methods = c.getMethods();
		List<Method> list = new ArrayList<Method>();
		for (Method m : methods) {
			if (c.isAnnotationPresent(Interceptor.class)
					&& (m.getName().equals("before") || m.getName().equals(
							"after"))) { // 验证拦截器annotation和方法名
				list.add(m);
			}
		}
		return list;
	}

	/**
	 * 根据拦截器模式获取所有注册的Uri
	 *
	 * @param pattern
	 * @return
	 */
	private List<String> getInterceptUri(String pattern) {
		List<String> list = new ArrayList<String>();
		for (String uriAndMethod : uriList) {
			String uri = StringUtils.split(uriAndMethod, "@")[1];
			if(ignoreBackslashEquals(pattern, uri)) {
				log.debug("intercept uri[{}] pattern[{}]", uri, pattern);
				list.add(uri);
			}
		}
		return list;
	}

	/**
	 * 拦截地址匹配，忽略uri和pattern最后的'/'
	 * @param pattern
	 * @param uri
	 * @return
	 */
	private boolean ignoreBackslashEquals(String pattern, String uri) {
		if (uri.charAt(uri.length() - 1) == '/')
			uri = uri.substring(0, uri.length() - 1);
		if (pattern.charAt(pattern.length() - 1) == '/')
			pattern = pattern.substring(0, pattern.length() - 1);
		return VerifyUtils.simpleWildcardMatch(pattern, uri);
	}

	private ViewHandle getViewHandle(String view) {
		ViewHandle viewHandle = null;
		if (view.equals(View.JSP)) {
			viewHandle = JspViewHandle.getInstance();
		} else if (view.equals(View.TEXT)) {
			viewHandle = TextViewHandle.getInstance();
		} else if (view.equals(View.REDIRECT)) {
			viewHandle = RedirectHandle.getInstance();
		} else if (view.equals(View.JSON)) {
			viewHandle = JsonViewHandle.getInstance();
		}
		return viewHandle;
	}
}
