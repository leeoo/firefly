package com.firefly.net;

import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class ClientSynchronizer<T> {
    private static Log log = LogFactory.getInstance().getLog("firefly-system");
    private SynchronousObject<T>[] objs;
    private final long timeout;
    private int size;
    
    public ClientSynchronizer() {
    	this(0, 0);
    }

    public ClientSynchronizer(int size, long timeout) {
        if (size <= 0)
            size = 1024 * 16;
        
        int i = 2;
        while(i < size)
        	i <<= 1;

        this.size = i;
        log.info("synchronizer size: {}", this.size);

        if (timeout > 0)
            this.timeout = timeout;
        else
            this.timeout = 5000;

        init();
        log.debug("client timeout {}", timeout);
    }

    public T get(int index) {
        log.debug("get index {}", index);
        return objs[index & (size - 1)].get(timeout);
    }

    public void put(T t, int index) {
        log.debug("put index {}", index);
        objs[index & (size - 1)].put(t);
    }
    
    public void reset(int index) {
    	objs[index & (size - 1)].reset();
    }

    @SuppressWarnings("unchecked")
	public void init() {
        objs = new SynchronousObject[size];

        for (int i = 0; i < objs.length; i++) {
            objs[i] = new SynchronousObject<T>();
        }

    }

}
