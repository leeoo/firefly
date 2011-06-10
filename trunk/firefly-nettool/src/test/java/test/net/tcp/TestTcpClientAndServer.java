package test.net.tcp;

import static org.hamcrest.Matchers.is;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.net.Client;
import com.firefly.net.ClientSynchronizer;
import com.firefly.net.Config;
import com.firefly.net.Handler;
import com.firefly.net.Server;
import com.firefly.net.Session;
import com.firefly.net.tcp.TcpClient;
import com.firefly.net.tcp.TcpServer;

public class TestTcpClientAndServer {
	private static Logger log = LoggerFactory.getLogger(TestTcpClientAndServer.class);

	@Test
	public void testHello() {
		Server server = new TcpServer();
		Config config = new Config();
		config.setHandleThreads(-1);
		config.setDecoder(new StringLineDecoder());
		config.setEncoder(new StringLineEncoder());
		config.setHandler(new SendFileHandler());
		server.setConfig(config);
		server.start("localhost", 9900);
//		new TcpServer("localhost", 9900, new StringLineDecoder(),
//				new StringLineEncoder(), new StringLineHandler()).start();

//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}

		final ClientSynchronizer clientSynchronizer = new ClientSynchronizer(
				5, 512, 1000);
		Client client = new TcpClient(new StringLineDecoder(),
				new StringLineEncoder(), new Handler() {

					@Override
					public void sessionOpened(Session session) {
						System.out.println("client session open |"
								+ session.getSessionId());
						log.debug("client session thread {}", Thread.currentThread().toString());
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
						log.debug("session interest ops {}", session.getInterestOps());
						log.debug("client session thread {}", Thread.currentThread().toString());
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
		log.debug("main thread {}", Thread.currentThread().toString());
		String ret = (String) clientSynchronizer.getReceive();
		log.info("receive[" + ret + "]");
		Assert.assertThat(ret, is("hello client"));

		for(int i = 0; i < 50; i++) {
			session.encode("test2");
			ret = (String) clientSynchronizer.getReceive();
			Assert.assertThat(ret, is("test2"));
		}

		session.encode("getfile");
		ret = (String) clientSynchronizer.getReceive();
		log.info("receive[" + ret + "]");
		Assert.assertThat(ret, is("zero copy file transfers"));

		session.encode("quit");
		ret = (String) clientSynchronizer.getReceive();
		log.info("receive[" + ret + "]");
		Assert.assertThat(ret, is("bye!"));
	}
}
