package com.firefly.utils.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimeProvider {
	private long interval;
	private ScheduledFuture<?> fireHandle;
	private volatile long current = System.currentTimeMillis();

	public TimeProvider() {
	}

	public TimeProvider(long interval) {
		this.interval = interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public void start() {
		ScheduledExecutorService scheduler = Executors
				.newSingleThreadScheduledExecutor();
		fireHandle = scheduler.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				current = System.currentTimeMillis();
			}
		}, 0, interval, TimeUnit.MILLISECONDS);
	}

	public void stop() {
		fireHandle.cancel(true);
	}

	public long currentTimeMillis() {
		return current;
	}
	
//	public static void main(String[] args) throws InterruptedException {
//		TimeProvider time = new TimeProvider(100);
//		time.start();
//		System.out.println(time.currentTimeMillis());
//		Thread.sleep(5000);
//		System.out.println(time.currentTimeMillis());
//	}

}
