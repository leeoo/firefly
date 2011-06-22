package com.test.sample.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.Interceptor;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

@Interceptor(uri = "/ttt*", order = 1)
public class Itest2Interceptor {
	private static Log log = LogFactory.getInstance().getLog("firefly-hello");

	public String before(HttpServletRequest request, HttpServletResponse response) {
		log.info("before 1 [{}]", request.getRequestURI());
		request.setAttribute("hello", "测试拦截器ttt");
		return "/index.jsp";
	}

	public String after(HttpServletRequest request, HttpServletResponse response) {
		log.info("after 1 [{}]", request.getRequestURI());
		return null;
	}
}
