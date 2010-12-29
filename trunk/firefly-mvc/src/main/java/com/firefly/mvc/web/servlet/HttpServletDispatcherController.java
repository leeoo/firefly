package com.firefly.mvc.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.mvc.web.DefaultWebContext;
import com.firefly.mvc.web.DispatcherController;
import com.firefly.mvc.web.WebContext;
import com.firefly.mvc.web.support.BeanHandle;

public class HttpServletDispatcherController implements DispatcherController {

	private static Logger log = LoggerFactory
			.getLogger(HttpServletDispatcherController.class);

	private WebContext webContext;
	private static final String REQUEST_CLASS_NAME = HttpServletRequest.class
			.getName();
	private static final String RESPONSE_CLASS_NAME = HttpServletResponse.class
			.getName();

	private HttpServletDispatcherController() {

	}

	private static class HttpServletDispatcherControllerHolder {
		private static HttpServletDispatcherController instance = new HttpServletDispatcherController();
	}

	public static HttpServletDispatcherController getInstance() {
		return HttpServletDispatcherControllerHolder.instance;
	}

	@Override
	public void dispatcher(HttpServletRequest request,
			HttpServletResponse response) {
		String encoding = webContext.getEncoding();
		try {
			request.setCharacterEncoding(encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		response.setCharacterEncoding(encoding);

		String uri = request.getRequestURI();
		String prePath = request.getContextPath() + request.getServletPath();
		String invokeUri = uri.substring(prePath.length());
		String key = request.getMethod().toUpperCase() + "@" + invokeUri;

		log.info("uri map [{}]", key);
		BeanHandle beanHandle = (BeanHandle) webContext.getBean(key);
		if (beanHandle != null) {
			// TODO 此处还需要完善 1)增加请求参数封装到javabean
			String[] paraNames = beanHandle.getParaClassNames();
			Object[] p = new Object[paraNames.length];
			for (int i = 0; i < p.length; i++) {
				if (paraNames[i].equals(REQUEST_CLASS_NAME)) {
					p[i] = request;
				}
				if (paraNames[i].equals(RESPONSE_CLASS_NAME)) {
					p[i] = response;
				}
			}

			Object ret = beanHandle.invoke(p);
			try {
				beanHandle.getViewHandle().render(request, response, ret);
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			response
					.setHeader("Content-Type", "text/html; charset=" + encoding);
			PrintWriter writer = null;
			try {
				writer = response.getWriter();
			} catch (IOException e) {
				e.printStackTrace();
			}
			writer
					.print("<html><body><h2>HTTP ERROR 404</h2><hr /><i><small>firefly mvc framework</small></i></body></html>");
			writer.close();
		}
	}

	@Override
	public void init(String initParam) {
		webContext = DefaultWebContext.getInstance().load(initParam);
	}

}
