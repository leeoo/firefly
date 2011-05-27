package com.firefly.utils.timer;

public class Config {
	private int maxTimers = 10; // wheel的格子数量
	private long interval = 100; // wheel旋转时间间隔
	private int initialDelay = 0; // wheel开始旋转的延时时间
	private int timerThreads = 1; // wheel旋转线程数量
	private int workerThreads = 0;//Runtime.getRuntime().availableProcessors(); // 任务处理线程数量

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
