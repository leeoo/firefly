package com.test.sample.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.Interceptor;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

@Interceptor(uri = "/i*t4", order = 2)
public class Itest4Interceptor {
	private static Log log = LogFactory.getInstance().getLog("firefly-hello");

	public void before(HttpServletRequest request, HttpServletResponse response) {
		log.info("before 4 [{}]", request.getRequestURI());
	}

	public void after(HttpServletRequest request, HttpServletResponse response) {
		log.info("after 4 [{}]", request.getRequestURI());
	}
}
