package com.firefly.server.http;

import com.firefly.mvc.web.servlet.HttpServletDispatcherController;
import com.firefly.net.Handler;
import com.firefly.net.Session;
import com.firefly.server.exception.HttpServerException;
import com.firefly.utils.VerifyUtils;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class HttpHandler implements Handler {
	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	private HttpServletDispatcherController servletController;
	private FileDispatcherController fileController;
	private Config config;
	private String appPrefix;

	public HttpHandler(HttpServletDispatcherController servletController,
			Config config) {
		this.servletController = servletController;
		this.config = config;
		appPrefix = config.getContextPath() + config.getServletPath();
		if (VerifyUtils.isEmpty(appPrefix))
			throw new HttpServerException(
					"context path and servlet path can not be null");

		fileController = new FileDispatcherController(config);
	}

	@Override
	public void sessionOpened(Session session) throws Throwable {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionClosed(Session session) throws Throwable {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageRecieved(Session session, Object message)
			throws Throwable {
		// TODO 这里要保证request处理顺序，需要使用阻塞队列，队列的数量可以配置，然后按照sessionId进行取模
		System.out.println("session id: " + session.getSessionId());
		HttpServletRequestImpl request = (HttpServletRequestImpl) message;
		if (request.response.system) {
			request.response.outSystemData();
		} else {
			if (isServlet(request.getRequestURI()))
				servletController.dispatcher(request, request.response);
			else
				fileController.dispatcher(request, request.response);
		}
	}

	private boolean isServlet(String URI) {
		if (URI.length() < 2)
			return false;

		int j = URI.length();
		for (int i = 1; i < URI.length(); i++) {
			if (URI.charAt(i) == '/') {
				j = i;
				break;
			}
		}

		if (j == URI.length())
			return appPrefix.equals(URI);
		else
			return appPrefix.equals(URI.substring(0, j));
	}

	@Override
	public void exceptionCaught(Session session, Throwable t) throws Throwable {
		log.error("server error", t);
		session.close(true);
	}

}
