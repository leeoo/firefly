package test.net.tcp;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.firefly.net.Client;
import com.firefly.net.Handler;
import com.firefly.net.Session;
import com.firefly.net.tcp.TcpClient;

public class TestTcpClient {
	public static void main(String[] args) {
		final BlockingQueue<Session> queue = new LinkedBlockingQueue<Session>();
		Client client = new TcpClient(new StringLineDecoder(), new StringLineEncoder(), new Handler() {

			@Override
			public void sessionOpened(Session session) {
				System.out.println("client session open |" + session.getSessionId());
				queue.offer(session);
			}

			@Override
			public void sessionClosed(Session session) {
				System.out.println("client session close|" + session.getSessionId());
			}

			@Override
			public void messageRecieved(Session session, Object message) {
				String str = (String) message;
				System.out.println("client receive[" + str + "]");
			}

			@Override
			public void exceptionCaught(Session session, Throwable t) {
				System.out.println(t.getMessage() + "|"
						+ session.getSessionId());
			}
		});
		client.connect("192.168.1.102", 9900);
		try {
			Session session = queue.take();
			session.encode("hello client");
			session.encode("test 3");
//			session.encode("quit");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
