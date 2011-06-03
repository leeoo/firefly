package com.firefly.net;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public interface Worker extends Runnable {

	void registerSocketChannel(SocketChannel socketChannel, int sessionId);

	void close(SelectionKey key);

	void setInterestOps(Session session, int interestOps);

	int getWorkerId();

	void fire(EventType eventType, final Session session, final Object message,
			final Throwable t);

	void writeFromUserCode(final Session session);

	void writeFromTaskLoop(final Session session);
}
