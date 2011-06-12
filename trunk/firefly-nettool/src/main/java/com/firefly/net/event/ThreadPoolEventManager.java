package com.firefly.net.event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.net.Config;
import com.firefly.net.EventManager;
import com.firefly.net.Session;

public class ThreadPoolEventManager implements EventManager {
	private static Logger log = LoggerFactory.getLogger(ThreadPoolEventManager.class);
	private ExecutorService executorService;
	private Config config;

	public ThreadPoolEventManager(Config config) {
		this.config = config;
		if (config.getHandleThreads() > 0) {
			log.debug("newFixedThreadPool: {}", config.getHandleThreads());
			executorService = Executors.newFixedThreadPool(config
					.getHandleThreads());
		} else if (config.getHandleThreads() == 0) {
			log.debug("newCachedThreadPool");
			executorService = Executors.newCachedThreadPool();
		}
	}
	
	public void shutdown() {
		executorService.shutdown();
		log.debug("executorService is shutdown: {}", executorService.isShutdown());
	}

	public void executeOpenTask(Session session) {
		executorService.submit(new OpenTask(session));
	}

	public void executeReceiveTask(Session session, Object message) {
		executorService.submit(new ReceiveTask(session, message));
	}

	public void executeCloseTask(Session session) {
		executorService.submit(new CloseTask(session));
	}

	public void executeExceptionTask(Session session, Throwable t) {
		executorService.submit(new ExceptionTask(session,t));
	}

	private class OpenTask implements Runnable {
		private Session session;

		private OpenTask(Session session) {
			this.session = session;
		}

		@Override
		public void run() {
			try {
				config.getHandler().sessionOpened(session);
			} catch (Throwable t) {
				ThreadPoolEventManager.this.executeExceptionTask(session, t);
			}
		}

	}

	private class ReceiveTask implements Runnable {
		private Session session;
		private Object message;

		private ReceiveTask(Session session, Object message) {
			this.session = session;
			this.message = message;
		}

		@Override
		public void run() {
			try {
				log.debug("thread pool event");
				config.getHandler().messageRecieved(session, message);
			} catch (Throwable t) {
				ThreadPoolEventManager.this.executeExceptionTask(session, t);
			}
		}
	}

	private class CloseTask implements Runnable {
		private Session session;

		private CloseTask(Session session) {
			this.session = session;
		}

		@Override
		public void run() {
			try {
				config.getHandler().sessionClosed(session);
			} catch (Throwable t) {
				ThreadPoolEventManager.this.executeExceptionTask(session, t);
			}
		}
	}

	private class ExceptionTask implements Runnable {
		private Session session;
		private Throwable t;

		private ExceptionTask(Session session, Throwable t) {
			this.session = session;
			this.t = t;
		}

		@Override
		public void run() {
			try {
				config.getHandler().exceptionCaught(session, t);
			} catch (Throwable t) {
				log.error("handler exception", t);
			}
		}
	}
}
