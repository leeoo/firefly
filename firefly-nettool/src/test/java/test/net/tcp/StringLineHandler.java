package test.net.tcp;

import com.firefly.net.Handler;
import com.firefly.net.Session;

public class StringLineHandler implements Handler {

	@Override
	public void sessionOpened(Session session) {
		System.out.println("session open |" + session.getSessionId());
		System.out.println("local: " + session.getLocalAddress());
		System.out.println("remote: " + session.getRemoteAddress());
	}

	@Override
	public void sessionClosed(Session session) {
		System.out.println("session close|" + session.getSessionId());
	}

	@Override
	public void messageRecieved(Session session, Object message) {
		String str = (String) message;
		if (str.equals("quit")) {
			session.encode("bye!");
			session.close();
		} else {
			System.out.println("recive: " + str);
			session.encode(message);
		}
	}

	@Override
	public void exceptionCaught(Session session, Throwable t) {
		System.out.println("session exception|" + session.getSessionId());
	}

}
