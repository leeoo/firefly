package com.test.sample.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.firefly.annotation.Controller;
import com.firefly.annotation.RequestMapping;
import com.firefly.mvc.web.HttpMethod;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

@Controller
public class FileUploadController {
	private static Log log = LogFactory.getInstance().getLog("firefly-hello");

	@RequestMapping(value = "/fileUpload")
	public String index(HttpServletRequest request) {
		return "/fileUpload.html";
	}

	@RequestMapping(value = "/upload", method=HttpMethod.POST)
	public String upload(HttpServletRequest request) {
		try {
			request.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "/fileUpload.html";
	}
}
