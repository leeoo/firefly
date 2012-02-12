package com.firefly.server.http;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.mvc.web.DispatcherController;
import com.firefly.mvc.web.servlet.SystemHtmlPage;
import com.firefly.server.exception.HttpServerException;
import com.firefly.server.io.StaticFileOutputStream;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class FileDispatcherController implements DispatcherController {

	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	private Config config;

	public FileDispatcherController(Config config) {
		this.config = config;
	}

	@Override
	public void dispatcher(HttpServletRequest request,
			HttpServletResponse response) {
		File file = new File(config.getServerHome(), request.getRequestURI());
		if (file.exists()) {
			response.setContentType("text/html; charset="
					+ config.getEncoding());
			StaticFileOutputStream out = null;
			try {
				out = ((HttpServletResponseImpl) response)
						.getStaticFileOutputStream();
				out.write(file);
			} catch (IOException e) {
				throw new HttpServerException(
						"get static file output stream error");
			} finally {
				if (out != null)
					try {
						out.close();
					} catch (IOException e) {
						throw new HttpServerException(
								"static file output stream close error");
					}
			}
		} else {
			SystemHtmlPage.responseSystemPage(request, response,
					config.getEncoding(), HttpServletResponse.SC_NOT_FOUND,
					request.getRequestURI() + " not found");
		}
	}

}
