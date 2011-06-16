package com.firefly.net;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public interface Worker extends Runnable {

	void registerSocketChannel(SelectableChannel selectableChannel, int sessionId);

	void close(SelectionKey key);

	int getWorkerId();

	EventManager getEventManager();

	void shutdown();
}
