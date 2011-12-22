package com.firefly.mvc.web.servlet;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class SystemHtmlPage {
	private static Log log = LogFactory.getInstance().getLog("firefly-system");

	public static void scNotFound(HttpServletRequest request,
			HttpServletResponse response, String charset) {
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
			writer.print("<html><body>");
			writer.print("<h2>HTTP ERROR 404</h2>");
			writer.print("<hr/><i><small>firefly mvc framework</small></i>");
			writer.print("</body></html>");
		} finally {
			if (writer != null)
				writer.close();
		}
	}
}
