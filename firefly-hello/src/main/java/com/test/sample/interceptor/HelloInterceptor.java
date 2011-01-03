package com.test.sample.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.annotation.Interceptor;
import com.firefly.mvc.web.View;

@Interceptor(uri = "/itest*", view = View.REDIRECT)
public class HelloInterceptor {
	private static Logger log = LoggerFactory.getLogger(HelloInterceptor.class);

	public void before(HttpServletRequest request, HttpServletResponse response) {
		log.info("before 0 [{}]", request.getRequestURI());
	}

	public void after(HttpServletRequest request, HttpServletResponse response) {
		log.info("after 0 [{}]", request.getRequestURI());
	}
}
