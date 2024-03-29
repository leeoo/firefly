package com.test.sample.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {

	private static final long serialVersionUID = -2285190638550535784L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println(request.getContextPath());
		request.setAttribute("hello", "你好 firefly!");
		request.getRequestDispatcher("/WEB-INF/page/index.jsp").forward(
				request, response);
	}
}
