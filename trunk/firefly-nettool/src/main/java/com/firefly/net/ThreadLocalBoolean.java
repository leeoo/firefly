package com.firefly.net;

public class ThreadLocalBoolean extends ThreadLocal<Boolean> {
	private final boolean defaultValue;

	public ThreadLocalBoolean() {
		this(false);
	}

	public ThreadLocalBoolean(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	@Override
	protected Boolean initialValue() {
		return defaultValue ? Boolean.TRUE : Boolean.FALSE;
	}
}
