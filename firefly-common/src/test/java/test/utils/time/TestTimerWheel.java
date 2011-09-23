package test.utils.time;

import com.firefly.utils.time.wheel.Config;
import com.firefly.utils.time.wheel.TimeWheel;

public class TestTimerWheel {
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
		final long start = System.currentTimeMillis();
		t.add(400, new Runnable() {

			@Override
			public void run() {
				long end = System.currentTimeMillis();
				System.out.println("t1: " + (end - start));
			}
		});

		t.add(2500, new Runnable() {

			@Override
			public void run() {
				long end = System.currentTimeMillis();
				System.out.println("t2: " + (end - start));
				t.add(1200, new Runnable() {

					@Override
					public void run() {
						long end = System.currentTimeMillis();
						System.out.println("t2: " + (end - start));
					}
				});
			}
		});
	}

	public static void main(String[] args) {
		new TestTimerWheel().test();
	}
}
