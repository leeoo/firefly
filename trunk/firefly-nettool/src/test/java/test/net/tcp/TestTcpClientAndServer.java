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
        final StringLineClientHandler handler = new StringLineClientHandler(LOOP, 1024 * 4);
        final Client client = new TcpClient(new StringLineDecoder(),
                new StringLineEncoder(), handler);


        for (int i = 0; i < LOOP; i++) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    final int sessionId = client.connect("localhost", 9900);
                    final Session session = handler.getSession(sessionId);

                    String message = "hello client";
                    int revId = handler.getRevId(session.getSessionId(), message);
                    session.encode(message);
                    log.debug("main thread {}", Thread.currentThread().toString());
                    String ret = handler.getReceive(revId);
                    log.debug("receive[" + ret + "]");
                    Assert.assertThat(ret, is("hello client"));

                    message = "hello multithread test";
                    revId = handler.getRevId(session.getSessionId(), message);
                    session.encode(message);
                    ret = handler.getReceive(revId);
                    Assert.assertThat(ret, is("hello multithread test"));

                    message = "getfile";
                    revId = handler.getRevId(session.getSessionId(), message);
                    session.encode(message);
                    ret = handler.getReceive(revId);
                    log.debug("receive[" + ret + "]");
                    Assert.assertThat(ret, is("zero copy file transfers"));

                    message = "quit";
                    revId = handler.getRevId(session.getSessionId(), message);
                    session.encode(message);
                    ret = handler.getReceive(revId);
                    log.debug("receive[" + ret + "]");
                    Assert.assertThat(ret, is("bye!"));
                }
            });

        }

        final int sessionId = client.connect("localhost", 9900);
        final Session session = handler.getSession(sessionId);

        String message = "hello client 2";
        int revId = handler.getRevId(session.getSessionId(), message);
        session.encode(message);
        log.debug("main thread {}", Thread.currentThread().toString());
        String ret = handler.getReceive(revId);
        log.debug("receive[" + ret + "]");
        Assert.assertThat(ret, is("hello client 2"));

        message = "quit";
        revId = handler.getRevId(session.getSessionId(), message);
        session.encode(message);
        ret = handler.getReceive(revId);
        log.debug("receive[" + ret + "]");
        Assert.assertThat(ret, is("bye!"));

//        server.shutdown();
//        client.shutdown();
    }
}
