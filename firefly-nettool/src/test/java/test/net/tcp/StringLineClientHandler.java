package test.net.tcp;

import com.firefly.net.Handler;
import com.firefly.net.Session;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class StringLineClientHandler implements Handler {

	private static Log log = LogFactory.getInstance().getLog("firefly-system");

    @Override
    public void sessionOpened(Session session) {
        log.debug("session: {} open", session.getSessionId());
    }

    @Override
    public void sessionClosed(Session session) {
        log.debug("session: {} close", session.getSessionId());
    }

    @Override
    public void messageRecieved(Session session, Object message) {
        log.debug("message: {}", message);
        session.setResult(message, 1000);
    }

    @Override
    public void exceptionCaught(Session session, Throwable t) {
        log.error("client session error", t);
    }

	@Override
	public void writeComplete(Session session) {
		log.debug("written size: {}", session.getWrittenBytes());
		log.debug("written time: {}", session.getLastWrittenTime());
	}

}
