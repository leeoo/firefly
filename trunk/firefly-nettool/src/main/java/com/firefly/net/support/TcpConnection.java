package com.firefly.net.support;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.firefly.net.Session;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class TcpConnection {
	private Session session;
	private long timeout;
	private BlockingQueue<MessageReceiveCallBack> queue;
	public static final String QUEUE_KEY = "#message_queue";
	private static Log log = LogFactory.getInstance().getLog("firefly-system");

	public TcpConnection(Session session) {
		this(session, 0);
	}

	public TcpConnection(Session session, int queueLength) {
		this(session, queueLength, 0);
	}
	
	public TcpConnection(Session session, int queueLength, long timeout) {
		this.queue = new ArrayBlockingQueue<MessageReceiveCallBack>(
				queueLength > 0 ? queueLength : 1024 * 4);
		this.session = session;
		this.session.setAttribute(QUEUE_KEY, queue);
		this.timeout = timeout > 0 ? timeout : 5000L;
	}

	public Object send(Object obj) {
		final SynchronousObject<Object> ret = new SynchronousObject<Object>();
		send(obj, new MessageReceiveCallBack() {

			@Override
			public void messageRecieved(Session session, Object obj) {
				ret.put(obj, timeout);
			}
		});

		return ret.get(timeout);
	}

	public void send(Object obj, MessageReceiveCallBack callback) {
		boolean offer = false;
		try {
			offer = queue.offer(callback, timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			log.error("TcpConnection send exception", e);
		}
		if (offer)
			session.encode(obj);
		else
			log.warn("tcp connection queue is full");
	}

	public int getId() {
		return session.getSessionId();
	}

	public void close(boolean b) {
		session.close(b);
	}

	public boolean isOpen() {
		return session.isOpen();
	}

	public Session getSession() {
		return session;
	}
	
	public int getCallBackQueueSize() {
		return queue.size();
	}
}
