package test.net.tcp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.net.ClientSynchronizer;
import com.firefly.net.Handler;
import com.firefly.net.Session;

public class StringLineClientHandler implements Handler {

	private static Logger log = LoggerFactory
			.getLogger(StringLineClientHandler.class);
	private ClientSynchronizer clientSynchronizer = new ClientSynchronizer(5,
			1024, 1000);

	public Session getSession() {
		return clientSynchronizer.getSession();
	}

	public String getReceive() {
		return (String) clientSynchronizer.getReceive();
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
		clientSynchronizer.putReceive(str);
	}

	@Override
	public void exceptionCaught(Session session, Throwable t) {
		log.error("session error", t);
	}

}
