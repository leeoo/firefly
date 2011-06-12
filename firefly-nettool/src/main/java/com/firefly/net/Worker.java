package com.firefly.net;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public interface Worker extends Runnable {

	void registerSocketChannel(SocketChannel socketChannel, int sessionId);

	void close(SelectionKey key);

	int getWorkerId();

	EventManager getEventManager();
	
	void shutdown();
}
