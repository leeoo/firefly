package com.firefly.net.tcp;

import java.nio.channels.SocketChannel;

import com.firefly.net.Config;
import com.firefly.net.Worker;

public class TcpWorker implements Worker {
	
	private Config config;

	@Override
	public void setConfig(Config config) {
		this.config = config;
	}

	@Override
	public void registerSocketChannel(SocketChannel socketChannel, int sessionId) {
		// TODO Auto-generated method stub
		
	}

}
