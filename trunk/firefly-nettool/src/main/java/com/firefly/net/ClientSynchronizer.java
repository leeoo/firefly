package com.firefly.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientSynchronizer<T> {
    private static Logger log = LoggerFactory
            .getLogger(ClientSynchronizer.class);
    private SynchronousObject<Session>[] sessionArray;
    private SynchronousObject<T>[] receiveArray;
    private final long timeout;
    private int sessionSize, retSize;

    public ClientSynchronizer(int sessionSize, int retSize, long timeout) {
        if (sessionSize <= 0 || retSize <= 0)
            throw new IllegalArgumentException("sessionSize or retSize less than 1");

        this.sessionSize = sessionSize;
        this.retSize = retSize;
        if (timeout > 0)
            this.timeout = timeout;
        else
            this.timeout = 1000;

        init();
        log.debug("client timeout {}", timeout);
    }

    public Session getSession(int sessionId) {
        log.debug("get session {}", sessionId);
        return sessionArray[sessionId & (sessionSize - 1)].get(timeout);
    }

    public void putSession(Session session) {
        log.debug("put session {}", session.getSessionId());
        sessionArray[session.getSessionId() & (sessionSize - 1)].put(session);
    }

    public void putReceive(int revId, T t) {
        log.debug("put rev {}", revId);
        receiveArray[revId & (retSize - 1)].put(t);
    }

    public T getReceive(int revId) {
        log.debug("get rev {}", revId);
        return receiveArray[revId & (retSize - 1)].get(timeout);
    }

    public void init() {
        sessionArray = new SynchronousObject[sessionSize];
        receiveArray = new SynchronousObject[retSize];

        for (int i = 0; i < sessionArray.length; i++) {
            sessionArray[i] = new SynchronousObject<Session>();
        }

        for (int i = 0; i < receiveArray.length; i++) {
            receiveArray[i] = new SynchronousObject<T>();
        }
    }

}
