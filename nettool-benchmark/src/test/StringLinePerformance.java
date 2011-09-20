package test;

import com.firefly.net.Session;
import com.firefly.net.support.StringLineDecoder;
import com.firefly.net.support.StringLineEncoder;
import com.firefly.net.support.TcpConnection;
import com.firefly.net.support.MessageReceiveCallBack;
import com.firefly.net.support.SimpleTcpClient;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;
import java.util.concurrent.*;

public class StringLinePerformance {
	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	public static int LOOP;
	public static int THREAD;
	public static TcpConnection[] tcpConnections;
	public static String DATA;

	public static class ClientSynchronizeTask implements Runnable {

		private final CyclicBarrier barrier;
		private int id;

		@Override
		public void run() {
			TcpConnection c = tcpConnections[id];
			for (int i = 0; i < LOOP; i++) {
//				String message = "hello world! " + c.getId();
				String ret = (String) c.send(DATA);
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

		public ClientSynchronizeTask(int id, CyclicBarrier barrier) {
			this.id = id;
			this.barrier = barrier;
		}
	}

	public static class ClientAsynchronousTask implements Runnable {

		private final CyclicBarrier barrier;
		private int id;

		@Override
		public void run() {
			TcpConnection c = tcpConnections[id];
			for (int i = 0; i < LOOP; i++) {
//				String message = "hello world! " + c.getId();
				c.send(DATA, new MessageReceiveCallBack() {

					@Override
					public void messageRecieved(Session session, Object obj) {
						log.debug("rev: {}", obj);
					}
				});

			}
			c.send("quit", new MessageReceiveCallBack() {

				@Override
				public void messageRecieved(Session session, Object obj) {
					log.debug("rev: {}", obj);
					log.debug("session {} complete", session.getSessionId());
				}
			});
			try {
				barrier.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (BrokenBarrierException e) {
				e.printStackTrace();
			}

		}

		public ClientAsynchronousTask(int id, CyclicBarrier barrier) {
			this.id = id;
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
			log.info("throughput: {} req/s, {} KBytes/s", throughput, THREAD * LOOP * DATA.length() * 2L * 1000 / 1024.0 / time);
		}

	}

	public static void main(String[] args) {
//		System.setProperty("bind_host", "127.0.0.1");
//		System.setProperty("bind_port", "9900");
//		System.setProperty("loop", "2000");
//		System.setProperty("thread_num", "500");
//		System.setProperty("asyn", "0");
//		System.setProperty("size", "4096");
		
		String host = System.getProperty("bind_host");
		int port = Integer.parseInt(System.getProperty("bind_port"));
		int asyn = Integer.parseInt(System.getProperty("asyn"));
		int size = Integer.parseInt(System.getProperty("size"));

		LOOP = Integer.parseInt(System.getProperty("loop"));
		THREAD = Integer.parseInt(System.getProperty("thread_num"));
		tcpConnections = new TcpConnection[THREAD];
		log.info("threads: {}, loop: {}, total request: {}, total transferred: {}", THREAD, LOOP,
				THREAD * LOOP, THREAD * LOOP * (long)size);
		byte[] data = new byte[size / 2];
		for (int i = 0; i < data.length; i++) {
			data[i] = 80;
		}
		DATA = new String(data);
		

		ExecutorService executorService = Executors.newFixedThreadPool(THREAD);
		final SimpleTcpClient client = new SimpleTcpClient(host, port,
				new StringLineDecoder(), new StringLineEncoder());
		for (int i = 0; i < THREAD; i++) {
			tcpConnections[i] = client.connect();
		}
		final CyclicBarrier barrier = new CyclicBarrier(THREAD, new StatTask());

		if (asyn == 0) {
			log.info("synchronize test");
			for (int i = 0; i < THREAD; i++) {
				executorService.submit(new ClientSynchronizeTask(i, barrier));
			}
		} else {
			log.info("asynchronous test");
			for (int i = 0; i < THREAD; i++) {
				executorService.submit(new ClientAsynchronousTask(i, barrier));
			}
		}
	}
}
