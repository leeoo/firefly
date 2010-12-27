package com.firefly.mvc.web;

public interface WebContext {
	Object getBean(String id);

	String getViewPath();

	void load(String file);

	String getEncoding();
}
