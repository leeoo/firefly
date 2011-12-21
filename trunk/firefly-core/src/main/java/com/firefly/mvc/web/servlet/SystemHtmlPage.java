package com.firefly.mvc.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SystemHtmlPage {
	public static void scNotFound(HttpServletRequest request,
			HttpServletResponse response, String charset) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.setHeader("Content-Type",
				"text/html; charset=" + charset);
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.print("<html><body>");
		writer.print("<h2>HTTP ERROR 404</h2>");
		writer.print("<hr/><i><small>firefly mvc framework</small></i>");
		writer.print("</body></html>");
		writer.close();
	}
}
