package com.firefly.net;

import java.nio.channels.SocketChannel;

public interface Worker {
	void setConfig(Config config);
	
	void registerSocketChannel(SocketChannel socketChannel, int sessionId);
}
