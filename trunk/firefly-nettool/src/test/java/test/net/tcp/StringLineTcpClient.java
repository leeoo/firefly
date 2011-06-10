package test.net.tcp;

import com.firefly.net.Client;
import com.firefly.net.ClientSynchronizer;
import com.firefly.net.Handler;
import com.firefly.net.Session;
import com.firefly.net.tcp.TcpClient;

public class StringLineTcpClient {
	public static void main(String[] args) {
		final ClientSynchronizer clientSynchronizer = new ClientSynchronizer(
				5, 1024, 1000);
		Client client = new TcpClient(new StringLineDecoder(),
				new StringLineEncoder(), new Handler() {

					@Override
					public void sessionOpened(Session session) {
						System.out.println("client session open |"
								+ session.getSessionId());
						clientSynchronizer.putSession(session);
					}

					@Override
					public void sessionClosed(Session session) {
						System.out.println("client session close|"
								+ session.getSessionId());
					}

					@Override
					public void messageRecieved(Session session, Object message) {
						String str = (String) message;
						clientSynchronizer.putReceive(str);
					}

					@Override
					public void exceptionCaught(Session session, Throwable t) {
						System.out.println(t.getMessage() + "|"
								+ session.getSessionId());
					}
				});
		client.connect("localhost", 9900);

		Session session = clientSynchronizer.getSession();
		session.encode("hello client");
		String ret = (String) clientSynchronizer.getReceive();
		System.out.println("receive[" + ret + "]");

		session.encode("test2");
		ret = (String) clientSynchronizer.getReceive();
		System.out.println("receive[" + ret + "]");

		session.encode("quit");
		ret = (String) clientSynchronizer.getReceive();
		System.out.println("receive[" + ret + "]");

        client.connect("localhost", 9900);
        session = clientSynchronizer.getSession();
        session.encode("getfile");
        ret = (String) clientSynchronizer.getReceive();
		System.out.println("receive[" + ret + "]");

        session.encode("quit");
		ret = (String) clientSynchronizer.getReceive();
		System.out.println("receive[" + ret + "]");
	}
}
