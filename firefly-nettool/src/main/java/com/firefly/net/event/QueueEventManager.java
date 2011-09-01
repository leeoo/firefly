package com.firefly.net.event;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.firefly.net.Config;
import com.firefly.net.EventManager;
import com.firefly.net.Session;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class QueueEventManager implements EventManager {
	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	private Config config;
	private BlockingQueue<EventObject> queue = new ArrayBlockingQueue<EventObject>(
			65535);
	private volatile boolean start = false;

	public QueueEventManager(Config config) {
		this.config = config;
	}

	@Override
	public void executeOpenTask(Session session) {
		try {
			config.getHandler().sessionOpened(session);
		} catch (Throwable t) {
			executeExceptionTask(session, t);
		}
	}

	@Override
	public void executeReceiveTask(Session session, Object message) {
		EventObject eventObject = new EventObject();
		eventObject.session = session;
		eventObject.message = message;
		try {
			queue.offer(eventObject, 3000L, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.error("queue offer interrupted", e);
		}

	}

	@Override
	public void executeCloseTask(Session session) {
		try {
			config.getHandler().sessionClosed(session);
		} catch (Throwable t) {
			executeExceptionTask(session, t);
		}
	}

	@Override
	public void executeExceptionTask(Session session, Throwable t) {
		try {
			config.getHandler().exceptionCaught(session, t);
		} catch (Throwable t0) {
			log.error("handler exception", t0);
		}

	}

	private class EventObject {
		public Session session;
		public Object message;
	}

	private class EventTask implements Runnable {

		@Override
		public void run() {
			while (true) {
				try {
					for (EventObject e = null; (e = queue.poll(1000L,
							TimeUnit.MILLISECONDS)) != null;) {
						try {
							log.debug("QueueEventTask");
							config.getHandler().messageRecieved(e.session,
									e.message);
						} catch (Throwable t) {
							executeExceptionTask(e.session, t);
						}
					}
				} catch (InterruptedException e) {
					log.error("queue poll interrupted", e);
				}

				if (!start && queue.isEmpty())
					break;
			}
		}

	}

	public void shutdown() {
		start = false;
	}

	public void start() {
		if (!start) {
			synchronized (this) {
				if (!start) {
					start = true;
					new Thread(new EventTask(), "Queue event manager").start();
				}
			}
		}
	}

}
