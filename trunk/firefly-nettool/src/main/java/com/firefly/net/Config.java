package com.firefly.net;

public class Config {
	private int handleThreads = 1024;
	private int workerThreads = Runtime.getRuntime().availableProcessors() * 2;
	private int connectionTime, latency, bandwidth;
	private int backlog = 10240;
	private int timeout = 30000;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getBacklog() {
		return backlog;
	}

	public void setBacklog(int backlog) {
		this.backlog = backlog;
	}

	public int getHandleThreads() {
		return handleThreads;
	}

	public void setHandleThreads(int handleThreads) {
		this.handleThreads = handleThreads;
	}

	public int getWorkerThreads() {
		return workerThreads;
	}

	public void setWorkerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
	}

	public int getConnectionTime() {
		return connectionTime;
	}

	public void setConnectionTime(int connectionTime) {
		this.connectionTime = connectionTime;
	}

	public int getLatency() {
		return latency;
	}

	public void setLatency(int latency) {
		this.latency = latency;
	}

	public int getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(int bandwidth) {
		this.bandwidth = bandwidth;
	}

}
