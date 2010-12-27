package com.test.sample.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.annotation.Component;
import com.firefly.annotation.RequestMapping;
import com.firefly.mvc.web.WebContext;
import com.firefly.mvc.web.WebContextHolder;

@Component("test2Hello")
public class Test2Controller {
	private static Logger log = LoggerFactory.getLogger(Test2Controller.class);
	private int i = 0;

	@RequestMapping(value = "/hello2")
	public String index(HttpServletResponse response, HttpServletRequest request) {
		WebContext webContext = WebContextHolder.getWebContext();
		Test2Controller t = (Test2Controller) webContext.getBean("test2Hello");
		t.test();
		t = (Test2Controller) webContext.getBean(Test2Controller.class
				.getName());
		t.test();

		request.setAttribute("hello", "test2 hello hypercube! ");

		return "/index.jsp";
	}

	@RequestMapping(value = "/hello3")
	public String hello3(HttpServletRequest request,
			HttpServletResponse response) {
		try {
			log.info("hello3 req encoding [{}]", request.getCharacterEncoding());
			log.info("hello3 res encoding [{}]", response.getCharacterEncoding());
			response.setHeader("Content-Type", "text/plain; charset=utf-8");
			PrintWriter writer = response.getWriter();
			writer.print("测试hello3");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public void test() {
		log.info("test single [{}]", i++);
	}
}
