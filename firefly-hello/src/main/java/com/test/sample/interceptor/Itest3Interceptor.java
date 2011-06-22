package com.test.sample.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.Interceptor;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

@Interceptor(uri = "/ttt*", order = 2)
public class Itest3Interceptor {
	private static Log log = LogFactory.getInstance().getLog("firefly-hello");

	public String before(HttpServletRequest request, HttpServletResponse response) {
		log.info("before 2 [{}]", request.getRequestURI());
		request.setAttribute("hello", "测试拦截器2ttt");
		return "/index.jsp";
	}

	public void after(HttpServletRequest request, HttpServletResponse response) {
		log.info("after 2 [{}]", request.getRequestURI());
	}
}
