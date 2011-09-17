package com.firefly.net.support;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.firefly.net.Session;

public class TcpConnection {
	private Session session;
	private long timeout = 5000L;
	private BlockingQueue<MessageReceiveCallBack> queue;
	public static final String QUEUE_KEY = "#message_queue";

	public TcpConnection(Session session) {
		this(session, 0);
	}

	public TcpConnection(Session session, int size) {
		this.queue = new ArrayBlockingQueue<MessageReceiveCallBack>(
				size > 0 ? size : 1024 * 8);
		this.session = session;
		this.session.setAttribute(QUEUE_KEY, queue);
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
			e.printStackTrace();
		}
		if (offer)
			session.encode(obj);
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
