package test.net.tcp;

import com.firefly.net.Client;
import com.firefly.net.Session;
import com.firefly.net.tcp.TcpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.*;

public class StringLinePerformance {
    private static Logger log = LoggerFactory
            .getLogger(StringLinePerformance.class);
    public static final int LOOP = 2000;
    public static final int THREAD = 500;

    public static class ClientTask implements Runnable {

        private final StringLineClientHandler handler;
        private final Client client;
        private final CyclicBarrier barrier;

        public Session getSession() {
            int sessionId = client.connect("localhost", 9900);
            return handler.getSession(sessionId);
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
            session.close(false);
            log.debug("session {} complete", session.getSessionId());

            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

        }

        public ClientTask(StringLineClientHandler handler, Client client, CyclicBarrier barrier) {
            this.handler = handler;
            this.client = client;
            this.barrier = barrier;
        }
    }

    public static class StatTask implements Runnable {

        private long start;

        public StatTask() {
            this.start = System.currentTimeMillis();
        }

        @Override
        public void run() {
            long time = System.currentTimeMillis() - start;
            log.debug("start time: {}", start);
            log.debug("total time: {}", time);
            int reqs = LOOP * THREAD;

            double throughput = (reqs / (double) time) * 1000;
            log.info("throughput: {}", throughput);
        }

    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD);
        final StringLineClientHandler handler = new StringLineClientHandler(
                THREAD * 2);
        final Client client = new TcpClient(new StringLineDecoder(),
                new StringLineEncoder(), handler);

        final CyclicBarrier barrier = new CyclicBarrier(THREAD, new StatTask());

        for (int i = 0; i < THREAD; i++) {
            executorService.submit(new ClientTask(handler, client, barrier));
        }
    }
}
