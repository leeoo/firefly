package com.firefly.template;

import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class Config {
	public static Log LOG = LogFactory.getInstance().getLog("firefly-system");
	private String viewPath;
	private String compiledPath;
	private String suffix = "html";
	private String charset = "UTF-8";
	public static final String COMPILED_FOLDER_NAME = "_compiled_view";

	public String getViewPath() {
		return viewPath;
	}

	public void setViewPath(String viewPath) {
		char ch = viewPath.charAt(viewPath.length() - 1);
		this.viewPath = (ch == '/' || ch == '\\' ? viewPath : viewPath + "/")
				.replace('\\', '/');
		compiledPath = this.viewPath + COMPILED_FOLDER_NAME;
	}

	public String getCompiledPath() {
		return compiledPath;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
