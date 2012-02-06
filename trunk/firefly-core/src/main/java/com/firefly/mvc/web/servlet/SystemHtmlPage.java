package com.firefly.mvc.web.servlet;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class SystemHtmlPage {
	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	
	public static final Map<Integer, String> SYS_PAGE = new HashMap<Integer, String>();
	
	static {
		SYS_PAGE.put(404, "<!DOCTYPE html><html><body><h2>HTTP ERROR 404</h2><hr/><i><small>firefly mvc framework</small></i></body></html>");
	}

	public static void responseSystemPage(HttpServletRequest request,
			HttpServletResponse response, String charset, int status) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.setCharacterEncoding(charset);
		response.setHeader("Content-Type", "text/html; charset=" + charset);
		PrintWriter writer = null;
		try {
			try {
				writer = response.getWriter();
			} catch (Throwable t) {
				log.error("scNotFound error", t);
			}
			writer.print(SYS_PAGE.get(status));
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
