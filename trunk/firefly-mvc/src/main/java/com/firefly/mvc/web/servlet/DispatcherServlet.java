package com.firefly.mvc.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.mvc.web.DefaultWebContext;
import com.firefly.mvc.web.View;
import com.firefly.mvc.web.WebContext;
import com.firefly.mvc.web.support.BeanHandle;

/**
 * mvc前端控制器Servlet
 *
 * @author alvinqiu
 *
 */
public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = -3638120056786910984L;
	private static Logger log = LoggerFactory
			.getLogger(DispatcherServlet.class);
	private static final String INIT_PARAM = "contextConfigLocation";
	private static final String DEFAULT_CONFIG = "firefly_mvc.properties";
	private static final String REQUEST_CLASS_NAME = HttpServletRequest.class
			.getName();
	private static final String RESPONSE_CLASS_NAME = HttpServletResponse.class
			.getName();
	private WebContext webContext;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processDispatcher(request, response);

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processDispatcher(request, response);
	}

	@Override
	public void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processDispatcher(request, response);

	}

	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processDispatcher(request, response);
	}

	protected void processDispatcher(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String encoding = webContext.getEncoding();
		request.setCharacterEncoding(encoding);
		response.setCharacterEncoding(encoding);

		String uri = request.getRequestURI();
		String prePath = request.getContextPath() + request.getServletPath();
		String viewPath = webContext.getViewPath();
		String invokeUri = uri.substring(prePath.length());
		String key = request.getMethod().toUpperCase() + "@" + invokeUri;

		log.info("uri map [{}]", key);
		BeanHandle beanHandle = (BeanHandle) webContext.getBean(key);
		String view = beanHandle.getView();
		// TODO 此处还需要完善 1)增加请求参数封装到javabean, 2)反射调用方法参数柔性处理, 3)json返回的处理
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

		Object o = beanHandle.invoke(p);

		if (view.equals(View.JSP) && o instanceof String && o != null) {
			log.info("view [{}]", View.JSP);
			request.getRequestDispatcher(viewPath + o.toString()).forward(
					request, response);
		}
		if (view.equals(View.TEXT) && o instanceof String && o != null) {
			log.info("view [{}]", View.TEXT);
			response.setHeader("Content-Type", "text/plain; " + encoding);
			PrintWriter writer = response.getWriter();
			writer.print(o.toString());
			writer.close();
		}
	}

	@Override
	public void init() {
		webContext = DefaultWebContext.getInstance();
		String initParam = this.getInitParameter(INIT_PARAM);
		if (initParam == null)
			initParam = DEFAULT_CONFIG;
		log.info("initParam [{}]", initParam);
		webContext.load(initParam);
	}

}
