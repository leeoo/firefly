package com.test.sample.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Interceptor(uri = "/hello*")
public class HelloInterceptor {
	private static Logger log = LoggerFactory.getLogger(HelloInterceptor.class);

	public void before(HttpServletRequest request, HttpServletResponse response) {
		log.info("before [{}]", request.getRequestURI());
	}

	public String after(HttpServletRequest request, HttpServletResponse response) {
		log.info("after [{}]", request.getRequestURI());
		return null;
	}
}
