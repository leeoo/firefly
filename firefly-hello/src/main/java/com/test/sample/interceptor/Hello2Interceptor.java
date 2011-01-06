package com.test.sample.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.firefly.annotation.Interceptor;
import com.firefly.mvc.web.View;

@Interceptor(uri = "/ti", view = View.REDIRECT)
public class Hello2Interceptor {
	private static Logger log = LoggerFactory
			.getLogger(Hello2Interceptor.class);

	public String before(HttpServletRequest request,
			HttpServletResponse response) {
		log.info("ti before [{}]", request.getRequestURI());
//		return "/hello2";
		return null;
	}

	public String after(HttpServletRequest request, HttpServletResponse response) {
		log.info("ti after [{}]", request.getRequestURI());
		return "/hello3";
	}
}
