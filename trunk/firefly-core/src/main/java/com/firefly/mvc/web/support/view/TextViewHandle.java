package com.firefly.mvc.web.support.view;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.mvc.web.View;
import com.firefly.mvc.web.support.ViewHandle;

public class TextViewHandle implements ViewHandle {

	private static Logger log = LoggerFactory.getLogger(TextViewHandle.class);
	private String encoding;

	private TextViewHandle() {

	}

	private static class TextViewHandleHolder {
		private static TextViewHandle instance = new TextViewHandle();
	}

	public static TextViewHandle getInstance() {
		return TextViewHandleHolder.instance;
	}

	public TextViewHandle init(String encoding) {
		this.encoding = encoding;
		return this;
	}

	@Override
	public void render(HttpServletRequest request,
			HttpServletResponse response, Object view) throws ServletException,
			IOException {
		if (view instanceof String && view != null) {
			log.debug("view [{}]", View.TEXT);
			response.setCharacterEncoding(encoding);
			response.setHeader("Content-Type", "text/plain; charset="
					+ encoding);
			PrintWriter writer = response.getWriter();
			writer.print(view.toString());
			writer.close();
		}

	}

}
