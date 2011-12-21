package com.test.sample.controller;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.Controller;
import com.firefly.annotation.HttpParam;
import com.firefly.annotation.Inject;
import com.firefly.annotation.RequestMapping;
import com.firefly.mvc.web.HttpMethod;
import com.firefly.mvc.web.View;
import com.firefly.template.Function;
import com.firefly.template.FunctionRegistry;
import com.firefly.template.Model;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;
import com.test.sample.model.Book;
import com.test.sample.model.Book2;
import com.test.sample.model.HelloJson;
import com.test.sample.service.AddService;

@Controller
public class HelloController {
	private static Log log = LogFactory.getInstance().getLog("firefly-hello");
	private AddService addService;

	@Inject
	public void init(AddService addService) {
		this.addService = addService;
		FunctionRegistry.add("url", new Function() {

			@Override
			public void render(Model model, OutputStream out, Object... objs)
					throws Throwable {
				String url = (String) objs[0];
				byte[] ret = ("http://localhost:8081/firefly-demo" + url).getBytes("UTF-8");
				out.write(ret);
			}
		});
	}

	@RequestMapping(value = "/hello")
	public String index(HttpServletRequest request) {
		request.setAttribute("hello", "你好 firefly!");
		return "/index.html";
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
		return "/book.html";
	}

	@RequestMapping(value = "/hello/value2")
	public String helloValue2(@HttpParam("book") Book book) {
		book.setSell(true);
		book.setText("测试book");
		return "/book.html";
	}

	@RequestMapping(value = "/book/add")
	public String gotoCreateBook() {
		return "/book_create.html";
	}

	@RequestMapping(value = "/book/create", method = HttpMethod.POST)
	public String createBook(@HttpParam("book") Book book,
			@HttpParam Book2 book2) {
		book.setSell(true);
		book.setText("测试当前book");
		book.setId(90);
		log.info("book2 price [{}]", book2.getPrice());
		return "/book.html";
	}

}
