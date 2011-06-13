package com.firefly.net;

public class SynchronousObject<T> {
    private T obj;

    public void put(T obj) {
        synchronized (this) {
            this.obj = obj;
            notifyAll();
        }
    }

    public T get(long timeout) {
        if (obj != null) return obj;
        synchronized (this) {
            if (obj != null) return obj;
            try {
                wait(timeout);
            } catch (InterruptedException ie) {
            }
        }
        return obj;
    }

    public void reset() {
        obj = null;
    }
}
