package com.firefly.net.buffer;

public interface ReceiveBufferSizePredictor {
	int nextReceiveBufferSize();
	
	void previousReceiveBufferSize(int previousReceiveBufferSize);
}
