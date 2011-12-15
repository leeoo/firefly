package com.test.sample.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.Interceptor;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

@Interceptor(uri = "/itest*")
public class HelloInterceptor {
	private static Log log = LogFactory.getInstance().getLog("firefly-hello");

	public void before(HttpServletRequest request, HttpServletResponse response) {
		log.info("before 0 [{}]", request.getRequestURI());
	}

	public String after(HttpServletRequest request, HttpServletResponse response, String v, String v2) {
		log.info("after 0 [{}] v [{}] [{}]", request.getRequestURI(), v, v2);
		return v;
	}
}
