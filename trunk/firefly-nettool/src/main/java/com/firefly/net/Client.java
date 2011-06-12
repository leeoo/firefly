package com.firefly.net;

public interface Client {
	
	void setConfig(Config config);
	
	void connect(String host, int port);
	
	void shutdown();
}
