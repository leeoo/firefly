package com.test.sample.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.Component;
import com.firefly.annotation.Inject;
import com.firefly.annotation.RequestMapping;
import com.firefly.mvc.web.View;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;
import com.test.sample.service.AddService;

@Component("test2Hello")
public class Hello2Controller {
	private static Log log = LogFactory.getInstance().getLog("firefly-hello");
	@Inject
	private AddService addService;

	@RequestMapping(value = "/hello2")
	public String index(HttpServletResponse response, HttpServletRequest request) {
		// WebContext webContext = WebContextHolder.getWebContext();
		// Test2Controller t = (Test2Controller)
		// webContext.getBean("test2Hello");
		// t.test();
		// t = (Test2Controller) webContext.getBean(Test2Controller.class
		// .getName());
		// t.test();

		request.setAttribute("hello", "test2 hello firefly! ");

		return "/index.jsp";
	}

	@RequestMapping(value = "/hello3")
	public String hello3(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			log
					.info("hello3 req encoding [{}]", request
							.getCharacterEncoding());
			log.info("hello3 res encoding [{}]", response
					.getCharacterEncoding());
			response.setHeader("Content-Type", "text/plain; charset=utf-8");
			PrintWriter writer = response.getWriter();
			writer.print("测试hello3");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@RequestMapping(value = "/hello4")
	public String hello4(HttpServletRequest request,
			HttpServletResponse response) {
		int i = addService.add(99, 100);
		request.setAttribute("hello", "test2 hello inject! " + i);
		return "/index.jsp";
	}

	@RequestMapping(value = "/hello5", view = View.REDIRECT)
	public String hello5(HttpServletRequest request,
			HttpServletResponse response) {
		return "/hello";
	}

}
