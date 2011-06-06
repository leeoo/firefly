package com.firefly.net;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientConnectionPool {
	private static Logger log = LoggerFactory
			.getLogger(ClientConnectionPool.class);
	private final BlockingQueue<Session> sessionQueue;
	private final BlockingQueue<Object> receive;
	private final long timeout;

	public ClientConnectionPool(int sessionSize, int retSize, long timeout) {
		if (sessionSize > 0)
			sessionQueue = new ArrayBlockingQueue<Session>(sessionSize);
		else
			sessionQueue = new LinkedBlockingQueue<Session>();

		if (retSize > 0)
			receive = new ArrayBlockingQueue<Object>(retSize);
		else
			receive = new LinkedBlockingQueue<Object>();

		if (timeout > 0)
			this.timeout = timeout;
		else
			this.timeout = 0;
	}

	public Session getSession() {
		Session ret = null;
		try {
			if (timeout > 0) {
				log.debug("get session timeout {}", timeout);
				ret = sessionQueue.poll(timeout, TimeUnit.MILLISECONDS);
			} else {
				ret = sessionQueue.take();
			}
		} catch (InterruptedException e) {
			log.error("get session error", e);
		}
		return ret;
	}

	public Session getSession(long timeout) {
		Session ret = null;
		try {
			ret = sessionQueue.poll(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.error("get session error", e);
		}
		return ret;
	}

	public void putSession(Session session) {
		try {
			if (timeout > 0) {
				log.debug("put session timeout {}", timeout);
				sessionQueue.offer(session, timeout, TimeUnit.MILLISECONDS);
			} else {
				sessionQueue.put(session);
			}
		} catch (InterruptedException e) {
			log.error("put session error", e);
		}
	}

	public void putSession(Session session, long timeout) {
		try {
			sessionQueue.offer(session, timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.error("put session error", e);
		}
	}

	public Object getReceive() {
		Object ret = null;
		try {
			if (timeout > 0) {
				log.debug("get receive timeout {}", timeout);
				ret = receive.poll(timeout, TimeUnit.MILLISECONDS);
			} else {
				ret = receive.take();
			}
		} catch (InterruptedException e) {
			log.error("get session error", e);
		}
		return ret;
	}

	public Object getReceive(long timeout) {
		Object ret = null;
		try {
			ret = receive.poll(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.error("get session error", e);
		}
		return ret;
	}

	public void putReceive(Object object) {
		try {
			if (timeout > 0) {
				log.debug("put receive timeout {}", timeout);
				receive.offer(object, timeout, TimeUnit.MILLISECONDS);
			} else {
				receive.put(object);
			}
		} catch (InterruptedException e) {
			log.error("put session error", e);
		}
	}

	public void putReceive(Object object, long timeout) {
		try {
			receive.offer(object, timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.error("put session error", e);
		}
	}

}
