package com.test.sample.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.Controller;
import com.firefly.annotation.Inject;
import com.firefly.annotation.RequestMapping;
import com.firefly.mvc.web.View;
import com.test.sample.model.HelloJson;
import com.test.sample.service.AddService;

@Controller
public class HelloController {
	private AddService addService;

	@Inject
	public void init(AddService addService) {
		this.addService = addService;
	}

	@RequestMapping(value = "/hello")
	public String index(HttpServletRequest request) {
		request.setAttribute("hello", "你好 firefly!");
		return "/index.jsp";
	}

	@RequestMapping(value = "/hello1", view = View.TEXT)
	public String hello1(HttpServletResponse response,
			HttpServletRequest request) {
		return "测试一下 3 + 3 =" + addService.add(3, 3);
	}

	@RequestMapping(value = "/hello/json", view = View.JSON)
	public Object helloJson(HttpServletResponse response,
			HttpServletRequest request) {
		HelloJson helloJson = new HelloJson();
		helloJson.setId(3);
		helloJson.setText("测试json");
		return helloJson;
	}
}
