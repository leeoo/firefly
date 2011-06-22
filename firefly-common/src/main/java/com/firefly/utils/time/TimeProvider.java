package com.firefly.utils.time;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class TimeProvider {
	private long interval;
	private ScheduledFuture<?> fireHandle;
	private ScheduledExecutorService scheduler;
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
		scheduler = Executors
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
		scheduler.shutdown();
	}

	public long currentTimeMillis() {
		return current;
	}

}
