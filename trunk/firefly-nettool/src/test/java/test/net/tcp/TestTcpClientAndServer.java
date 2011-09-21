package test.net.tcp;

import com.firefly.net.Config;
import com.firefly.net.Server;
import com.firefly.net.Session;
import com.firefly.net.support.MessageReceiveCallBack;
import com.firefly.net.support.StringLineDecoder;
import com.firefly.net.support.StringLineEncoder;
import com.firefly.net.support.TcpConnection;
import com.firefly.net.support.SimpleTcpClient;
import com.firefly.net.tcp.TcpServer;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.Matchers.is;

public class TestTcpClientAndServer {
	private static Log log = LogFactory.getInstance().getLog("firefly-system");

	@Test
	public void testHello() {
		Server server = new TcpServer();
		Config config = new Config();
		config.setHandleThreads(100);
		config.setDecoder(new StringLineDecoder());
		config.setEncoder(new StringLineEncoder());
		config.setHandler(new SendFileHandler());
		server.setConfig(config);
		server.start("localhost", 9900);

		final int LOOP = 50;
		ExecutorService executorService = Executors.newFixedThreadPool(LOOP);
		final SimpleTcpClient client = new SimpleTcpClient("localhost", 9900,
				new StringLineDecoder(), new StringLineEncoder());

		for (int i = 0; i < LOOP; i++) {
			executorService.submit(new Runnable() {
				@Override
				public void run() {
					final TcpConnection c = client.connect();
					Assert.assertThat(c.isOpen(), is(true));

					log.debug("main thread {}", Thread.currentThread()
							.toString());
					String ret = (String) c.send("hello client");
					log.debug("receive[" + ret + "]");
					Assert.assertThat(ret, is("hello client"));

					ret = (String) c.send("hello multithread test");
					Assert.assertThat(ret, is("hello multithread test"));

					ret = (String) c.send("getfile");
					log.debug("receive[" + ret + "]");
					Assert.assertThat(ret, is("zero copy file transfers"));

					ret = (String) c.send("quit");
					log.debug("receive[" + ret + "]");
					Assert.assertThat(ret, is("bye!"));
					log.debug("complete session {}", c.getId());
				}
			});

		}

		final TcpConnection c = client.connect();

		log.debug("main thread {}", Thread.currentThread().toString());
		c.send("hello client 2", new MessageReceiveCallBack() {

			@Override
			public void messageRecieved(Session session, Object obj) {
				Assert.assertThat((String) obj, is("hello client 2"));

			}
		});

		c.send("quit", new MessageReceiveCallBack() {

			@Override
			public void messageRecieved(Session session, Object obj) {
				Assert.assertThat((String) obj, is("bye!"));
				log.debug("complete session {}", c.getId());
			}
		});

	}
}
