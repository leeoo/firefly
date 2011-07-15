package com.firefly.net;

public class SynchronousObject<T> {
	private volatile T obj;

	public synchronized void put(T obj) {
		this.obj = obj;
		notifyAll();
	}

	public T get(long timeout) {
		if (obj == null) {
			synchronized (this) {
				if (obj == null)
					try {
						wait(timeout);
					} catch (InterruptedException ie) {
					}
			}
		}
		return obj;
	}

	public void reset() {
		obj = null;
	}
}
