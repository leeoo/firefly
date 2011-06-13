package test.net.tcp;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.net.Client;
import com.firefly.net.Session;
import com.firefly.net.tcp.TcpClient;

public class StringLinePerformance {
	private static Logger log = LoggerFactory
			.getLogger(StringLinePerformance.class);
	public static final int LOOP = 10;
	public static final int THREAD = 10;

	public static class ClientTask implements Runnable {

		private final StringLineClientHandler handler;
		private final Client client;
		private final CyclicBarrier barrier;

		public ClientTask(CyclicBarrier barrier, StringLineClientHandler handler, Client client) {
			this.handler = handler;
            this.client = client;
			this.barrier = barrier;
		}

		@Override
		public void run() {
			int sessionId = client.connect("localhost", 9900);
			Session session = handler.getSession(sessionId);
			for (int i = 0; i < LOOP; i++) {
                String message = "hello world! " + session.getSessionId();
                int revId = handler.getRevId(session.getSessionId(), message);
                log.info("put revid {}", revId);
		        session.encode(message);
				String ret = handler.getReceive(revId);
				log.debug("rev: {}", ret);
			}
			session.close(false);
            log.info("session {} complete", sessionId);
//			client.shutdown();
			try {
				barrier.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}

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
			
			double throughput = (reqs / (double)time) * 1000;
			log.info("throughput: {}", throughput);
		}
		
	}

	public static void main(String[] args) {
		ExecutorService executorService = Executors.newFixedThreadPool(THREAD);
        final StringLineClientHandler handler = new StringLineClientHandler(THREAD, 1024 * 4);
        final Client client = new TcpClient(new StringLineDecoder(),
					new StringLineEncoder(), handler);
		final CyclicBarrier barrier = new CyclicBarrier(THREAD, new StatTask());

		for(int i = 0; i < THREAD; i++) {
			executorService.submit(new ClientTask(barrier, handler, client));
		}
	}
}
