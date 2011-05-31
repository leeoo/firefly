package com.firefly.net;

import java.nio.ByteBuffer;

public interface Session {
	void setAttribute(String key, Object value);

	Object getAttribute(String key);

	void removeAttribute(String key);

	void clearAttributes();

	void executeHandler(Object message);

	void encode(Object message);

	void write(ByteBuffer byteBuffer);
}
