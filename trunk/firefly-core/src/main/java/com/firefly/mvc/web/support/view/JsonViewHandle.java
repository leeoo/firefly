package com.firefly.mvc.web.support.view;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.firefly.mvc.web.View;
import com.firefly.mvc.web.support.ViewHandle;

public class JsonViewHandle implements ViewHandle {

	private static Logger log = LoggerFactory.getLogger(TextViewHandle.class);
	private String encoding;
	private ObjectMapper mapper = new ObjectMapper();

	private JsonViewHandle() {

	}

	private static class JsonViewHandleHolder {
		private static JsonViewHandle instance = new JsonViewHandle();
	}

	public static JsonViewHandle getInstance() {
		return JsonViewHandleHolder.instance;
	}

	public JsonViewHandle init(String encoding) {
		this.encoding = encoding;
		return this;
	}

	@Override
	public void render(HttpServletRequest request,
			HttpServletResponse response, Object view) throws ServletException,
			IOException {
		if (view != null) {
			log.info("view [{}]", View.JSON);
			response.setCharacterEncoding(encoding);
			response.setHeader("Content-Type", "application/json; charset="
					+ encoding);
			PrintWriter writer = response.getWriter();
			mapper.writeValue(writer, view);
			writer.close();
		}
	}

}
