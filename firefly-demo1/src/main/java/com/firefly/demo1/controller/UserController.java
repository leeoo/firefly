package com.firefly.demo1.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.Controller;
import com.firefly.annotation.Inject;
import com.firefly.annotation.RequestMapping;
import com.firefly.demo1.service.UserService;

@Controller
public class UserController {
	@Inject
	private UserService userService;

	@RequestMapping(value = "/users")
	public String list(HttpServletResponse response, HttpServletRequest request) {
		request.setAttribute("users", userService.getUsers());
		return "/users.html";
	}
}
