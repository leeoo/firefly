package com.firefly.utils.timer;

public class Config {
	private int maxTimers = 10;
	private long interval = 100;
	private int initialDelay = 0;
	private int timerThreads = 1;
	private int workerThreads = Runtime.getRuntime().availableProcessors();

	public int getInitialDelay() {
		return initialDelay;
	}

	public void setInitialDelay(int initialDelay) {
		this.initialDelay = initialDelay;
	}

	public int getWorkerThreads() {
		return workerThreads;
	}

	public void setWorkerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
	}

	public int getMaxTimers() {
		return maxTimers;
	}

	public void setMaxTimers(int maxTimers) {
		this.maxTimers = maxTimers;
	}

	public long getInterval() {
		return interval;
	}

	public void setInterval(long interval) {
		this.interval = interval;
	}

	public int getTimerThreads() {
		return timerThreads;
	}

	public void setTimerThreads(int timerThreads) {
		this.timerThreads = timerThreads;
	}

}
