package test.net.tcp;

import com.firefly.net.Client;
import com.firefly.net.ClientConnectionPool;
import com.firefly.net.Handler;
import com.firefly.net.Session;
import com.firefly.net.tcp.TcpClient;

public class TestTcpClient {
	public static void main(String[] args) {
		final ClientConnectionPool clientConnectionPool = new ClientConnectionPool(
				1024, 1024, 1000);
		Client client = new TcpClient(new StringLineDecoder(),
				new StringLineEncoder(), new Handler() {

					@Override
					public void sessionOpened(Session session) {
						System.out.println("client session open |"
								+ session.getSessionId());
						clientConnectionPool.putSession(session);
					}

					@Override
					public void sessionClosed(Session session) {
						System.out.println("client session close|"
								+ session.getSessionId());
					}

					@Override
					public void messageRecieved(Session session, Object message) {
						String str = (String) message;
						clientConnectionPool.putReceive(str);
					}

					@Override
					public void exceptionCaught(Session session, Throwable t) {
						System.out.println(t.getMessage() + "|"
								+ session.getSessionId());
					}
				});
		client.connect("192.168.1.102", 9900);

		Session session = clientConnectionPool.getSession();
		session.encode("hello client");
		String ret = (String) clientConnectionPool.getReceive();
		System.out.println("receive[" + ret + "]");

		session.encode("test2");
		ret = (String) clientConnectionPool.getReceive();
		System.out.println("receive[" + ret + "]");

		session.encode("quit");
		ret = (String) clientConnectionPool.getReceive();
		System.out.println("receive[" + ret + "]");

	}
}
