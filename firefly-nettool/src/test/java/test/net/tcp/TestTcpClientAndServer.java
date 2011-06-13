package test.net.tcp;

import com.firefly.net.Client;
import com.firefly.net.Config;
import com.firefly.net.Server;
import com.firefly.net.Session;
import com.firefly.net.tcp.TcpClient;
import com.firefly.net.tcp.TcpServer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.Matchers.is;

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

        final int LOOP = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(LOOP);
        final StringLineClientHandler handler = new StringLineClientHandler(LOOP * 2);
        final Client client = new TcpClient(new StringLineDecoder(),
                new StringLineEncoder(), handler);


        for (int i = 0; i < LOOP; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    final int sessionId = client.connect("localhost", 9900);
                    final Session session = handler.getSession(sessionId);

		            session.encode("hello client");
                    log.debug("main thread {}", Thread.currentThread().toString());
                    String ret = (String)session.getResult(1000);
                    log.debug("receive[" + ret + "]");
                    Assert.assertThat(ret, is("hello client"));

		            session.encode("hello multithread test");
                    ret = (String)session.getResult(1000);
                    Assert.assertThat(ret, is("hello multithread test"));

		            session.encode("getfile");
                    ret = (String)session.getResult(1000);
                    log.debug("receive[" + ret + "]");
                    Assert.assertThat(ret, is("zero copy file transfers"));

		            session.encode("quit");
                    ret = (String)session.getResult(1000);
                    log.debug("receive[" + ret + "]");
                    Assert.assertThat(ret, is("bye!"));
                    log.debug("complete session {}", sessionId);
                }
            });

        }

        final int sessionId = client.connect("localhost", 9900);
        final Session session = handler.getSession(sessionId);

		session.encode("hello client 2");
        log.debug("main thread {}", Thread.currentThread().toString());
        String ret = (String)session.getResult(1000);
        log.debug("receive[" + ret + "]");
        Assert.assertThat(ret, is("hello client 2"));

		session.encode("quit");
        ret = (String)session.getResult(1000);
        log.debug("receive[" + ret + "]");
        Assert.assertThat(ret, is("bye!"));
        log.debug("complete session {}", sessionId);

//        server.shutdown();
//        client.shutdown();
    }
}
