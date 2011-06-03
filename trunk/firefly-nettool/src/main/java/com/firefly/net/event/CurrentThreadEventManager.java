package com.firefly.net.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.net.Config;
import com.firefly.net.EventManager;
import com.firefly.net.Session;

public class CurrentThreadEventManager implements EventManager {
	private static Logger log = LoggerFactory.getLogger(CurrentThreadEventManager.class);
	private Config config;

	public CurrentThreadEventManager(Config config) {
		this.config = config;
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
		try {
			log.debug("CurrentThreadEventManager");
			config.getHandler().messageRecieved(session, message);
		} catch (Throwable t) {
			executeExceptionTask(session, t);
		}
	}

}
