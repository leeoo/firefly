package com.firefly.net;

public interface Session {
	void setAttribute(String key, Object value);

	Object getAttribute(String key);

	void removeAttribute(String key);

	void clearAttributes();
	
	void executeHandler(Object message);
	
	void write(Object message);
}
