package com.test.sample.controller;

import javax.servlet.http.HttpServletRequest;

import com.firefly.annotation.Component;
import com.firefly.annotation.RequestMapping;
import com.firefly.core.ApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component("test2Hello")
public class Test2Controller {
	private static Logger log = LoggerFactory.getLogger(Test2Controller.class);
	private int i = 0;

	@RequestMapping(value = "/hello2")
	public String index(HttpServletRequest request) {
		Test2Controller t = (Test2Controller)ApplicationContext.getInstance().getBean("test2Hello");
		t.test();
		t = (Test2Controller)ApplicationContext.getInstance().getBean(Test2Controller.class.getName());
		t.test();

		request.setAttribute("hello", "test2 hello hypercube! ");

		return "/index.jsp";
	}

	public void test() {
		log.info("test single [{}]", i++);
	}
}
