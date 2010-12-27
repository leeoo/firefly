package com.firefly.mvc.web;

abstract public class WebContextHolder {
	public static WebContext getWebContext() {
		return DefaultWebContext.getInstance();
	}
}
