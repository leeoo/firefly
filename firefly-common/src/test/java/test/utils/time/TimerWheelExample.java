package test.utils.time;

import com.firefly.utils.time.TimeProvider;
import com.firefly.utils.time.wheel.Config;
import com.firefly.utils.time.wheel.TimeWheel;

public class TimerWheelExample {
	public void test() {
		final TimeWheel t = new TimeWheel();
		Config config = new Config();
		config.setMaxTimers(5);
		config.setInterval(100);
		t.setConfig(config);
		t.start();
		
		try {
			Thread.sleep(130L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		t.add(400, new Runnable() {
			private long start = System.currentTimeMillis();

			@Override
			public void run() {
				long end = System.currentTimeMillis();
				System.out.println("t1: " + (end - start));
			}
		});

		t.add(2500, new Runnable() {
			private long start = System.currentTimeMillis();
			
			@Override
			public void run() {
				long end = System.currentTimeMillis();
				System.out.println("t2: " + (end - start));
				t.add(1200, new Runnable() {
					private long start = System.currentTimeMillis();
					@Override
					public void run() {
						long end = System.currentTimeMillis();
						System.out.println("t2: " + (end - start));
					}
				});
			}
		});
	}

	public static void main(String[] args) throws InterruptedException {
		new TimerWheelExample().test();
		TimeProvider t = new TimeProvider(1000L);
		t.start();
		
		Thread.sleep(1000L);
		long start = t.currentTimeMillis();
		Thread.sleep(5000L);
		System.out.println("TimeProvider: " + (t.currentTimeMillis() - start));
	}
}
