package test.net.tcp;

import com.firefly.net.Client;
import com.firefly.net.Session;
import com.firefly.net.tcp.TcpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.*;

public class StringLinePerformance {
    private static Logger log = LoggerFactory
            .getLogger(StringLinePerformance.class);
    public static final int LOOP = 2000;
    public static final int THREAD = 10;

    public static class ClientTask implements Runnable {

        // private final StringLineClientHandler handler;
        // private final Client client;
        private final CyclicBarrier barrier;
        private final Queue<Session> sessionPool;

        public Session getSession() {
            Session session = sessionPool.poll();
            if (session == null || !session.isOpen()) {
                final StringLineClientHandler handler = new StringLineClientHandler(
                        1);
                final Client client = new TcpClient(new StringLineDecoder(),
                        new StringLineEncoder(), handler);
                int sessionId = client.connect("localhost", 9900);
                session = handler.getSession(sessionId);
                log.info("new session {}", sessionId);
            }
            return session;
        }

        public void release(Session session) {
            if (session != null && session.isOpen())
                sessionPool.offer(session);
        }

        @Override
        public void run() {
            Session session = getSession();
            for (int i = 0; i < LOOP; i++) {
                String message = "hello world! " + session.getSessionId();
                session.encode(message);
                String ret = (String) session.getResult(1000);
                log.debug("rev: {}", ret);
            }

            log.debug("session {} complete", session.getSessionId());
            release(session);

            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

        }

        public ClientTask(CyclicBarrier barrier, Queue<Session> sessionPool) {
            super();
            this.barrier = barrier;
            this.sessionPool = sessionPool;
        }

    }

    public static class StatTask implements Runnable {

        private long start;
        private final Queue<Session> sessionPool;

        public StatTask(Queue<Session> sessionPool) {
            this.start = System.currentTimeMillis();
            this.sessionPool = sessionPool;
        }

        @Override
        public void run() {
            long time = System.currentTimeMillis() - start;
            log.debug("start time: {}", start);
            log.debug("total time: {}", time);
            int reqs = LOOP * THREAD;

            double throughput = (reqs / (double) time) * 1000;
            log.info("throughput: {}", throughput);
            Session session;
            while ((session = sessionPool.poll()) != null) {
                session.close(false);
            }
        }

    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD);

        Queue<Session> sessionPool = new ConcurrentLinkedQueue<Session>();
        final StringLineClientHandler handler = new StringLineClientHandler(
                THREAD * 2);
        final Client client = new TcpClient(new StringLineDecoder(),
                new StringLineEncoder(), handler);
        for (int i = 0; i < THREAD; i++) {
//            final StringLineClientHandler handler = new StringLineClientHandler(
//                    THREAD * 2);
//            final Client client = new TcpClient(new StringLineDecoder(),
//                    new StringLineEncoder(), handler);

            int sessionId = client.connect("localhost", 9900);
            Session session = handler.getSession(sessionId);
            sessionPool.offer(session);
        }
        log.info(sessionPool.toString());

        final CyclicBarrier barrier = new CyclicBarrier(THREAD, new StatTask(sessionPool));

        for (int i = 0; i < THREAD; i++) {
            executorService.submit(new ClientTask(barrier, sessionPool));
        }
    }
}
