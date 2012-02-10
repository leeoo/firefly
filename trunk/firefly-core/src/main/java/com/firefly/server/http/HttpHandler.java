package com.firefly.server.http;

import com.firefly.mvc.web.servlet.HttpServletDispatcherController;
import com.firefly.net.Handler;
import com.firefly.net.Session;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class HttpHandler implements Handler {
	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	private HttpServletDispatcherController controller;
	private Config config;

	public HttpHandler(HttpServletDispatcherController controller, Config config) {
		this.controller = controller;
		this.config = config;
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
		HttpServletRequestImpl request = (HttpServletRequestImpl) message;
		controller.dispatcher(request, request.response);
	}

	@Override
	public void exceptionCaught(Session session, Throwable t) throws Throwable {
		// TODO Auto-generated method stub

	}

}
