package com.firefly.net;

public interface Session {
	void setAttribute(String key, Object value);

	Object getAttribute(String key);

	void removeAttribute(String key);

	void clearAttributes();

	Handler getHandler();

	void setHandler(Handler handler);

	Decoder getDecoder();

	void setDecoder(Decoder decoder);

	Encoder getEncoder();

	void setEncoder(Encoder encoder);
	
	void executeHandler();
	
	
}
