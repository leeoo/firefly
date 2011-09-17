package com.firefly.net.support;

import java.util.Queue;

import com.firefly.net.Handler;
import com.firefly.net.Session;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class SimpleTcpClientHandler implements Handler {

	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	private Synchronizer<Session> synchronizer;
	
	public SimpleTcpClientHandler(Synchronizer<Session> synchronizer) {
		this.synchronizer = synchronizer;
	}

    @Override
    public void sessionOpened(Session session) {
        log.debug("session: {} open", session.getSessionId());
        synchronizer.put(session, session.getSessionId());
    }

    @Override
    public void sessionClosed(Session session) {
        log.debug("session: {} close", session.getSessionId());
    }

    @SuppressWarnings("unchecked")
	@Override
    public void messageRecieved(Session session, Object message) {
        log.debug("message: {}", message);
        Queue<MessageReceiveCallBack> queue = (Queue<MessageReceiveCallBack>)session.getAttribute(TcpConnection.QUEUE_KEY);
        queue.poll().messageRecieved(session, message);
    }

    @Override
    public void exceptionCaught(Session session, Throwable t) {
        log.error("client session error", t);
    }

}
