package com.test.sample.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.Controller;
import com.firefly.annotation.RequestMapping;
import com.firefly.mvc.web.View;

@Controller
public class InterceptorTestController {
	@RequestMapping(value = "/itest1")
	public String index(HttpServletRequest request) {
		request.setAttribute("hello", "你好 itest1!");
		return "/index.html";
	}

	@RequestMapping(value = "/itest2/t2")
	public String index2(HttpServletRequest request) {
		request.setAttribute("hello", "你好 itest2!");
		return "/index.html";
	}

	@RequestMapping(value = "/itest4")
	public String index3(HttpServletRequest request) {
		request.setAttribute("hello", "你好 itest4!");
		return "/index.html";
	}

	@RequestMapping(value = "/ti", view = View.TEXT)
	public String ti(HttpServletResponse response, HttpServletRequest request) {
		return "测试interceptor";
	}

	@RequestMapping(value = "/ttt1", view = View.TEXT)
	public String ttt(HttpServletResponse response, HttpServletRequest request) {
		return "测试interceptor";
	}
}
