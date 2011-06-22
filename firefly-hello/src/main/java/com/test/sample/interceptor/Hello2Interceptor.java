package com.test.sample.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.firefly.annotation.Interceptor;
import com.firefly.mvc.web.View;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

@Interceptor(uri = "/ti", view = View.REDIRECT)
public class Hello2Interceptor {
	private static Log log = LogFactory.getInstance().getLog("firefly-hello");

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
