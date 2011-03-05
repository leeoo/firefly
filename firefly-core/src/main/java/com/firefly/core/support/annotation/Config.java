package com.firefly.core.support.annotation;

import com.firefly.utils.VerifyUtils;

public class Config {
	private String viewPath, encoding;
	private String[] paths;
	private static final String DEFAULT_VIEW_PATH = "/WEB-INF/page";
	private static final String DEFAULT_ENCODING = "UTF-8";

	public String getViewPath() {
		return VerifyUtils.isNotEmpty(viewPath) ? viewPath : DEFAULT_VIEW_PATH;
	}

	public void setViewPath(String viewPath) {
		this.viewPath = viewPath;
	}

	public String getEncoding() {
		return VerifyUtils.isNotEmpty(encoding) ? encoding : DEFAULT_ENCODING;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String[] getPaths() {
		return paths;
	}

	public void setPaths(String[] paths) {
		this.paths = paths;
	}

}
