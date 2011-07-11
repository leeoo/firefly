package com.firefly.net;

public interface Client {
	
	void setConfig(Config config);
	
	Session connect(String host, int port);
	
	void shutdown();
}
