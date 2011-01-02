package com.test.sample.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.annotation.Interceptor;

@Interceptor(uri = "/itest*/t*", order = 1)
public class ItestInterceptor {
	private static Logger log = LoggerFactory.getLogger(ItestInterceptor.class);

	public void before(HttpServletRequest request, HttpServletResponse response) {
		log.info("before 1 [{}]", request.getRequestURI());
	}

	public String after(HttpServletRequest request, HttpServletResponse response) {
		log.info("after 1 [{}]", request.getRequestURI());
		return null;
	}
}
