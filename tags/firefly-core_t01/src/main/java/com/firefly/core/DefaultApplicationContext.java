package com.firefly.core;

public class DefaultApplicationContext extends AbstractApplicationContext {

	private DefaultApplicationContext() {
	}

	private static class DefaultApplicationContextHolder {
		private static DefaultApplicationContext instance = new DefaultApplicationContext();
	}

	public static DefaultApplicationContext getInstance() {
		return DefaultApplicationContextHolder.instance;
	}

	@Override
	public void addObjectToContext(Class<?> c, Object o) {

	}

}
