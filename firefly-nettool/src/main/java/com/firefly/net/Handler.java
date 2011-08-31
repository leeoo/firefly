package com.firefly.net;

public interface Handler {
	void sessionOpened(Session session);
	void sessionClosed(Session session);
	void messageRecieved(Session session, Object message);
	void exceptionCaught(Session session, Throwable t);
	void writeComplete(Session session);
}
