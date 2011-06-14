package com.firefly.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientSynchronizer<T> {
    private static Logger log = LoggerFactory
            .getLogger(ClientSynchronizer.class);
    private SynchronousObject<T>[] objs;
    private final long timeout;
    private int size;

    public ClientSynchronizer(int size, long timeout) {
        if (size <= 0)
            throw new IllegalArgumentException("sessionSize or retSize less than 1");

        this.size = size;

        if (timeout > 0)
            this.timeout = timeout;
        else
            this.timeout = 1000;

        init();
        log.debug("client timeout {}", timeout);
    }

    public T get(int index) {
        log.debug("get index {}", index);
        return objs[index].get(timeout);
    }

    public void put(T t, int index) {
        log.debug("put index {}", index);
        objs[index].put(t);
    }

    @SuppressWarnings("unchecked")
	public void init() {
        objs = new SynchronousObject[size];

        for (int i = 0; i < objs.length; i++) {
            objs[i] = new SynchronousObject<T>();
        }

    }

}
