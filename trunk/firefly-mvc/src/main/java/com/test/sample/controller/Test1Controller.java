package com.test.sample.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.Controller;
import com.firefly.annotation.Inject;
import com.firefly.annotation.RequestMapping;
import com.firefly.mvc.web.View;
import com.test.sample.service.Test2Service;

@Controller
public class Test1Controller {
	private Test2Service test2Service;

	@SuppressWarnings("unused")
	@Inject
	private void init(Test2Service test2Service) {
		this.test2Service = test2Service;
	}

	@RequestMapping(value = "/hello")
	public String index(HttpServletRequest request) {

		request.setAttribute("hello", "##test hello firefly! ");

		return "/index.jsp";
	}

	@RequestMapping(value = "/hello1", view = View.TEXT)
	public String hello1(HttpServletResponse response,
			HttpServletRequest request) {

		return "测试一下 3 + 3 =" + test2Service.add(3, 3);
	}
}
