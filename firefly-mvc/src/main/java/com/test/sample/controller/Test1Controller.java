package com.test.sample.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.Controller;
import com.firefly.annotation.RequestMapping;
import com.firefly.mvc.web.View;

@Controller
public class Test1Controller {

	@RequestMapping(value = "/hello")
	public String index(HttpServletRequest request) {

		request.setAttribute("hello", "##test hello firefly! ");

		return "/index.jsp";
	}

	@RequestMapping(value = "/hello1", view = View.TEXT)
	public String hello1(HttpServletResponse response,
			HttpServletRequest request) {

		return "测试一下hello1";
	}
}
