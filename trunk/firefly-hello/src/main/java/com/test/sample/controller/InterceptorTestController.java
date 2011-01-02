package com.test.sample.controller;

import javax.servlet.http.HttpServletRequest;

import com.firefly.annotation.Controller;
import com.firefly.annotation.RequestMapping;

@Controller
public class InterceptorTestController {
	@RequestMapping(value = "/itest1")
	public String index(HttpServletRequest request) {
		request.setAttribute("hello", "你好 itest1!");
		return "/index.jsp";
	}

	@RequestMapping(value = "/itest2/t2")
	public String index2(HttpServletRequest request) {
		request.setAttribute("hello", "你好 itest2!");
		return "/index.jsp";
	}
}
