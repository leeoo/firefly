package com.firefly.net;

import java.nio.channels.SocketChannel;

public interface Worker extends Runnable {
	
	void registerSocketChannel(SocketChannel socketChannel, int sessionId);
}
