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
		SYS_PAGE.put(404, systemPageTemplate(404, "page not found"));
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
	
	public static String systemPageTemplate(int status, String content) {
		StringBuilder ret = new StringBuilder();
		ret.append("<!DOCTYPE html>")
		.append("<html>")
		.append("<body>")
		.append("<h2>")
			.append("HTTP ERROR").append(status)
		.append("</h2>")
		.append("<div>")
			.append(content)
		.append("</div>")
		.append("<hr/>")
		.append("<i>")
			.append("<small>")
				.append("firefly framework")
			.append("</small>")
		.append("</i>")
		.append("</body>")
		.append("</html>");
		return ret.toString();
	}
}
