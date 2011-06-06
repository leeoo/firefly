package com.firefly.net;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

public interface Session {
	int CLOSE = 0;
	int OPEN = 1;
	ByteBuffer CLOSE_FLAG = ByteBuffer.allocate(0);

	void setAttribute(String key, Object value);

	Object getAttribute(String key);

	void removeAttribute(String key);

	void clearAttributes();

	void fireReceiveMessage(Object message);

	void encode(Object message);

	void write(ByteBuffer byteBuffer);
	
	int getInterestOps();

	int getSessionId();

	long getOpenTime();

	void close(boolean immediately);

	int getState();

	boolean isOpen();

	InetSocketAddress getLocalAddress();

	InetSocketAddress getRemoteAddress();
}
