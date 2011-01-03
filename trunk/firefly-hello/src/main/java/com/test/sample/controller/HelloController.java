package com.test.sample.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.annotation.Controller;
import com.firefly.annotation.HttpParam;
import com.firefly.annotation.Inject;
import com.firefly.annotation.RequestMapping;
import com.firefly.mvc.web.HttpMethod;
import com.firefly.mvc.web.View;
import com.test.sample.model.Book;
import com.test.sample.model.Book2;
import com.test.sample.model.HelloJson;
import com.test.sample.service.AddService;

@Controller
public class HelloController {
	private static Logger log = LoggerFactory.getLogger(HelloController.class);
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

	@RequestMapping(value = "/hello/value")
	public String helloValue(HttpServletRequest request, @HttpParam Book book) {
		request.setAttribute("book", book);
		return "/book.jsp";
	}

	@RequestMapping(value = "/hello/value2")
	public String helloValue2(@HttpParam("book") Book book) {
		book.setSell(true);
		book.setText("测试book");
		return "/book.jsp";
	}

	@RequestMapping(value = "/book/add")
	public String gotoCreateBook() {
		return "/book_create.jsp";
	}

	@RequestMapping(value = "/book/create", method = HttpMethod.POST)
	public String createBook(@HttpParam("book") Book book, @HttpParam Book2 book2) {
		book.setSell(true);
		book.setText("测试当前book");
		book.setId(90);
		log.info("book2 price [{}]", book2.getPrice());
		return "/book.jsp";
	}

}
