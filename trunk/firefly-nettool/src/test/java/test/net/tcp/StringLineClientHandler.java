package test.net.tcp;

import com.firefly.net.ClientSynchronizer;
import com.firefly.net.Handler;
import com.firefly.net.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringLineClientHandler implements Handler {

    private static Logger log = LoggerFactory
            .getLogger(StringLineClientHandler.class);
    private ClientSynchronizer<String> clientSynchronizer;
    private int retSize;

    public StringLineClientHandler(int sessionSize, int retSize) {
        clientSynchronizer = new ClientSynchronizer<String>(sessionSize,
                retSize, 1000);
        this.retSize = retSize;
    }

    public Session getSession(int sessionId) {
        return clientSynchronizer.getSession(sessionId);
    }

    public String getReceive(int revId) {
        log.debug("get rev >>>>>>> {}", revId);
        return clientSynchronizer.getReceive(revId);
    }

    @Override
    public void sessionOpened(Session session) {
        log.debug("session: {} open", session.getSessionId());
        clientSynchronizer.putSession(session);
    }

    @Override
    public void sessionClosed(Session session) {
        log.debug("session: {} close", session.getSessionId());
    }

    @Override
    public void messageRecieved(Session session, Object message) {
        String str = (String) message;
        int id = getRevId(session.getSessionId(), str);
        log.debug("rev message id: {}", id);
        clientSynchronizer.putReceive(id, str);
    }

    @Override
    public void exceptionCaught(Session session, Throwable t) {
        log.error("session error", t);
    }

    public int getRevId(int sessionId, String message) {
        int result = sessionId;
        int messageCode = 0;
        if (message != null) {
            if (message.equals("quit"))
                messageCode = "bye!".hashCode();
            else if (message.equals("getfile"))
                messageCode = "zero copy file transfers".hashCode();
            else
                messageCode = message.hashCode();
        }
        result = 31 * result + messageCode;
        return Math.abs(result) % retSize;
    }

}
