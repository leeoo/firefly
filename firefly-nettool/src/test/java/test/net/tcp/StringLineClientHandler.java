package test.net.tcp;

import com.firefly.net.ClientSynchronizer;
import com.firefly.net.Handler;
import com.firefly.net.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringLineClientHandler implements Handler {

    private static Logger log = LoggerFactory
            .getLogger(StringLineClientHandler.class);
    private ClientSynchronizer<Session> clientSynchronizer;

    public StringLineClientHandler(int sessionSize) {
        clientSynchronizer = new ClientSynchronizer<Session>(sessionSize, 1000);
    }

    public Session getSession(int sessionId) {
        return clientSynchronizer.get(sessionId);
    }

    @Override
    public void sessionOpened(Session session) {
        log.debug("session: {} open", session.getSessionId());
        clientSynchronizer.put(session, session.getSessionId());
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
        log.error("session error", t);
    }

}
