package com.firefly.mvc.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FontController {

	public abstract void dispatcher(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	public abstract void load(String file);

	public abstract String getViewPath();

	public abstract String[] getComponentPath();

}