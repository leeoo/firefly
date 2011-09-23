package com.firefly.utils.time.wheel;

public class Config {
	private int maxTimers = 60; // wheel的格子数量
	private long interval = 1000; // wheel旋转时间间隔

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
