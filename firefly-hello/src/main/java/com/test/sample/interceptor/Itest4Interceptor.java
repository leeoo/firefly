package com.test.sample.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.annotation.Interceptor;

@Interceptor(uri = "/i*t4", order = 2)
public class Itest4Interceptor {
	private static Logger log = LoggerFactory.getLogger(Itest4Interceptor.class);

	public void before(HttpServletRequest request, HttpServletResponse response) {
		log.info("before 4 [{}]", request.getRequestURI());
	}

	public void after(HttpServletRequest request, HttpServletResponse response) {
		log.info("after 4 [{}]", request.getRequestURI());
	}
}
