package com.test.sample.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.annotation.Controller;
import com.firefly.annotation.RequestMapping;
import com.firefly.mvc.web.HttpMethod;

@Controller
public class FileUploadController {
	private static Logger log = LoggerFactory.getLogger(FileUploadController.class);

	@RequestMapping(value = "/fileUpload")
	public String index(HttpServletRequest request) {
		return "/fileUpload.jsp";
	}

	@RequestMapping(value = "/upload", method=HttpMethod.POST)
	public String upload(HttpServletRequest request) {
		try {
			request.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "/fileUpload.jsp";
	}
}
