package com.firefly.utils.time.wheel;

public class Config {
	private int maxTimers = 600; // wheel的格子数量
	private long interval = 100; // wheel旋转时间间隔
	private int workerThreads = 0; // 任务处理线程数量

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

}
