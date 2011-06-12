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
	public static final int LOOP = 2000;
	public static final int THREAD = 10;

	public static class ClientTask implements Runnable {

		private final StringLineClientHandler handler;
		private final Client client;
		private final CyclicBarrier barrier;

		public ClientTask(CyclicBarrier barrier) {
			handler = new StringLineClientHandler();
			client = new TcpClient(new StringLineDecoder(),
					new StringLineEncoder(), handler);

			this.barrier = barrier;
		}

		@Override
		public void run() {
			client.connect("localhost", 9900);
			Session session = handler.getSession();
			for (int i = 0; i < LOOP; i++) {
				session.encode("hello world!");
				String ret = (String) handler.getReceive();
				log.debug("receive ret: {}", ret);
			}
			session.close(false);
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
		CyclicBarrier barrier = new CyclicBarrier(THREAD, new StatTask());
		for(int i = 0; i < THREAD; i++) {
			executorService.submit(new ClientTask(barrier));
		}
	}
}
