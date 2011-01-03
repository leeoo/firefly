package com.test.sample.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.firefly.annotation.Interceptor;

@Interceptor(uri = "/ttt*", order = 1)
public class Itest2Interceptor {
	private static Logger log = LoggerFactory.getLogger(Itest2Interceptor.class);

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
