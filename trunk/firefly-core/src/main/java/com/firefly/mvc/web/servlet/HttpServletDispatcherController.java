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
		String key = request.getMethod() + "@" + invokeUri;
		String beforeIntercept = "before##intercept:" + key;
		String afterIntercept = "after##intercept:" + key;
		BeanHandle before = (BeanHandle) webContext.getBean(beforeIntercept);
		BeanHandle after = (BeanHandle) webContext.getBean(afterIntercept);

		log.info("uri map [{}]", key);
		BeanHandle beanHandle = (BeanHandle) webContext.getBean(key);
		if (beanHandle != null) {
			Object ret = null;
			Object beforeRet = null;
			Object afterRet = null;
			if (before != null) {
				Object[] beforeP = getParams(request, response, before);
				beforeRet = before.invoke(beforeP);
			}

			Object[] p = getParams(request, response, beanHandle);
			ret = beanHandle.invoke(p);

			if (after != null) {
				Object[] afterP = getParams(request, response, after);
				afterRet = after.invoke(afterP);
			}
			try {
				if (afterRet != null) {
					after.getViewHandle().render(request, response, afterRet);
					return;
				}
				if (beforeRet != null) {
					before.getViewHandle().render(request, response, beforeRet);
					return;
				}
				beanHandle.getViewHandle().render(request, response, ret);
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			scNotFound(request, response);
		}
	}

	public HttpServletDispatcherController init(String initParam) {
		webContext = DefaultWebContext.getInstance().load(initParam);
		return this;
	}

	private Object[] getParams(HttpServletRequest request,
			HttpServletResponse response, BeanHandle beanHandle) {
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
		return p;
	}

	private void scNotFound(HttpServletRequest request,
			HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.setHeader("Content-Type", "text/html; charset="
				+ webContext.getEncoding());
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