package com.firefly.mvc.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.firefly.annotation.RequestMapping;
import com.firefly.core.AnnotationApplicationContext;
import com.firefly.core.support.annotation.AnnotationBeanDefinition;
import com.firefly.core.support.annotation.ConfigReader;
import com.firefly.mvc.web.support.BeanHandle;
import com.firefly.mvc.web.support.ViewHandle;
import com.firefly.mvc.web.support.WebBeanDefinition;
import com.firefly.mvc.web.support.WebBeanReader;
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
public class AnnotationWebContext extends AnnotationApplicationContext
		implements WebContext {
	private static Logger log = LoggerFactory
			.getLogger(AnnotationWebContext.class);
	private List<String> uriList;

	public AnnotationWebContext() {
		this(null);
	}

	public AnnotationWebContext(String file) {
		super(file);
		uriList = new ArrayList<String>();
		JspViewHandle.getInstance().init(getViewPath());
		TextViewHandle.getInstance().init(getEncoding());
		JsonViewHandle.getInstance().init(getEncoding());
		for (AnnotationBeanDefinition beanDefinition : beanDefinitions) {
			addObjectToContext((WebBeanDefinition) beanDefinition);
		}
	}

	@Override
	protected List<AnnotationBeanDefinition> getBeanReader(String file) {
		return new WebBeanReader().loadBeanDefinitions();
	}

	@Override
	public String getEncoding() {
		return ConfigReader.getInstance().getConfig().getEncoding();
	}

	@Override
	public String getViewPath() {
		return ConfigReader.getInstance().getConfig().getViewPath();
	}

	@SuppressWarnings("unchecked")
	private void addObjectToContext(WebBeanDefinition beanDefinition) {
		// 注册Controller里面声明的uri
		List<Method> list = beanDefinition.getReqMethods();
		for (Method m : list) {
			m.setAccessible(true);
			final String uri = m.getAnnotation(RequestMapping.class).value();
			final String method = m.getAnnotation(RequestMapping.class)
					.method();
			final String view = m.getAnnotation(RequestMapping.class).view();
			String key = method + "@" + uri;

			BeanHandle beanHandle = new BeanHandle(beanDefinition.getObject(),
					m, getViewHandle(view));
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

		list = beanDefinition.getInterceptorMethods();
		for (Method m : list) {
			m.setAccessible(true);
			List<String> l = getInterceptUri(beanDefinition.getUriPattern());
			for (String i : l) {
				String key = m.getName().charAt(0) + "#" + i;
				BeanHandle beanHandle = new BeanHandle(beanDefinition
						.getObject(), m,
						getViewHandle(beanDefinition.getView()));
				beanHandle.setInterceptOrder(beanDefinition.getOrder());
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
	 * 根据拦截器模式获取所有注册的Uri
	 *
	 * @param pattern
	 * @return
	 */
	private List<String> getInterceptUri(String pattern) {
		List<String> list = new ArrayList<String>();
		for (String uriAndMethod : uriList) {
			String uri = StringUtils.split(uriAndMethod, "@")[1];
			if (ignoreBackslashEquals(pattern, uri)) {
				log.debug("intercept uri[{}] pattern[{}]", uri, pattern);
				list.add(uri);
			}
		}
		return list;
	}

	/**
	 * 拦截地址匹配，忽略uri和pattern最后的'/'
	 *
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
