package test.net.tcp;

import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;
import java.util.concurrent.*;

public class StringLinePerformance {
	private static Log log = LogFactory.getInstance().getLog("firefly-system");
    public static final int LOOP = 2000;
    public static final int THREAD = 500;

    public static class ClientSynchronizedTask implements Runnable {

        private final StringLineTcpClient client;
        private final CyclicBarrier barrier;

        @Override
        public void run() {
        	Connection c = client.connect();
            for (int i = 0; i < LOOP; i++) {
                String message = "hello world! " + c.getId();
                String ret = (String) c.send(message);
                log.debug("rev: {}", ret);
            }
            c.close(false);
            log.debug("session {} complete", c.getId());

            try {
                barrier.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }

        }

        public ClientSynchronizedTask(StringLineTcpClient client, CyclicBarrier barrier) {
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
        final StringLineTcpClient client = new StringLineTcpClient("localhost", 9900);
        final CyclicBarrier barrier = new CyclicBarrier(THREAD, new StatTask());

        for (int i = 0; i < THREAD; i++) {
            executorService.submit(new ClientSynchronizedTask(client, barrier));
        }
    }
}
