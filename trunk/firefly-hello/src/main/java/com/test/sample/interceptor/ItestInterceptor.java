package com.test.sample.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.Interceptor;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

@Interceptor(uri = "/itest*/t*", order = 1)
public class ItestInterceptor {
	private static Log log = LogFactory.getInstance().getLog("firefly-hello");

	public void before(HttpServletRequest request, HttpServletResponse response) {
		log.info("before 1 [{}]", request.getRequestURI());
	}

	public void after(HttpServletRequest request, HttpServletResponse response) {
		log.info("after 1 [{}]", request.getRequestURI());
	}
}
