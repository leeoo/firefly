package test.net.tcp;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.firefly.net.Client;
import com.firefly.net.ClientConnectionPool;
import com.firefly.net.Handler;
import com.firefly.net.Session;
import com.firefly.net.tcp.TcpClient;
import com.firefly.net.tcp.TcpServer;
import static org.hamcrest.Matchers.*;

public class TestTcpCs {
	private static Logger log = LoggerFactory.getLogger(TestTcpCs.class);

	@Test
	public void testHello() {
		new TcpServer("localhost", 9900, new StringLineDecoder(),
				new StringLineEncoder(), new StringLineHandler()).start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

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
						log.debug("session interest ops {}", session.getInterestOps());
					}

					@Override
					public void exceptionCaught(Session session, Throwable t) {
						System.out.println(t.getMessage() + "|"
								+ session.getSessionId());
					}
				});
		client.connect("localhost", 9900);

		Session session = clientConnectionPool.getSession();
		session.encode("hello client");
		String ret = (String) clientConnectionPool.getReceive();
		log.info("receive[" + ret + "]");
		Assert.assertThat(ret, is("hello client"));

		session.encode("test2");
		ret = (String) clientConnectionPool.getReceive();
		log.info("receive[" + ret + "]");
		Assert.assertThat(ret, is("test2"));

		session.encode("quit");
		ret = (String) clientConnectionPool.getReceive();
		log.info("receive[" + ret + "]");
		Assert.assertThat(ret, is("bye!"));
	}
}
