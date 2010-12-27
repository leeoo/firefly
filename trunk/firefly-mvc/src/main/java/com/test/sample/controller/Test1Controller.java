package com.test.sample.controller;

import javax.servlet.http.HttpServletRequest;

import com.firefly.annotation.Controller;
import com.firefly.annotation.RequestMapping;

@Controller
public class Test1Controller {

	@RequestMapping(value = "/hello")
	public String index(HttpServletRequest request) {

		request.setAttribute("hello", "##test hello firefly! ");

		return "/index.jsp";
	}

	@RequestMapping(value = "/hello1")
	public String hello1(HttpServletRequest request) {
		request.setAttribute("hello", "test1 hello firefly! ");

		return "/index.jsp";
	}
}
