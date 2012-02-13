package com.firefly.server.http;

import java.util.concurrent.TimeUnit;

import com.firefly.mvc.web.servlet.HttpServletDispatcherController;
import com.firefly.net.Handler;
import com.firefly.net.Session;
import com.firefly.server.exception.HttpServerException;
import com.firefly.utils.VerifyUtils;
import com.firefly.utils.collection.LinkedTransferQueue;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class HttpHandler implements Handler {
	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	private static Log access = LogFactory.getInstance().getLog(
			"firefly-access");
	private HttpServletDispatcherController servletController;
	private FileDispatcherController fileController;
	private String appPrefix;
	private HttpQueueHandler[] queues;

	public HttpHandler(HttpServletDispatcherController servletController,
			Config config) {
		this.servletController = servletController;
		appPrefix = config.getContextPath() + config.getServletPath();
		if (VerifyUtils.isEmpty(appPrefix))
			throw new HttpServerException(
					"context path and servlet path can not be null");

		fileController = new FileDispatcherController(config);
		queues = new HttpQueueHandler[config.getHandlerSize()];
		for (int i = 0; i < queues.length; i++) {
			queues[i] = new HttpQueueHandler(i);
		}
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
		HttpServletRequestImpl request = (HttpServletRequestImpl) message;
		int sessionId = session.getSessionId();
		int handlerIndex = Math.abs(sessionId) % queues.length;
		queues[handlerIndex].add(request);
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

	private class HttpQueueHandler {
		int id;
		boolean start = true;
		LinkedTransferQueue<HttpServletRequestImpl> queue = new LinkedTransferQueue<HttpServletRequestImpl>();
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (start) {
					try {
						for (HttpServletRequestImpl request = null; (request = queue.poll(
								1000, TimeUnit.MILLISECONDS)) != null;) {
							long start = com.firefly.net.Config.TIME_PROVIDER.currentTimeMillis();
							if (request.response.system) {
								request.response.outSystemData();
							} else {
								if (isServlet(request.getRequestURI()))
									servletController.dispatcher(request,
											request.response);
								else
									fileController.dispatcher(request,
											request.response);
							}
							long end = com.firefly.net.Config.TIME_PROVIDER
									.currentTimeMillis();
							access.info("{}|{}|{}|{}|{}|{}|{}|{}|{}|{}", 
									request.session.getSessionId(), 
									id, 
									request.session.getRemoteAddress().toString(),
									request.getProtocol(),
									request.getMethod(),
									request.getRequestURI(),
									request.getQueryString(),
									request.session.getReadBytes(),
									request.session.getWrittenBytes(),
									(end - start));
						}
					} catch (Throwable e) {
						log.error("http queue error", e);
					}
				}

			}
		}, "http queue " + id);

		public HttpQueueHandler(int id) {
			this.id = id;
			thread.start();
		}

		public void add(HttpServletRequestImpl request) {
			queue.offer(request);
		}

		public void shutdown() {
			start = false;
		}

	}

	public void shutdown() {
		for (HttpQueueHandler h : queues)
			h.shutdown();
	}
}
