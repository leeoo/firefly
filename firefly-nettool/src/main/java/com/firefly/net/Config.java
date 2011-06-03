package com.firefly.net;

import com.firefly.net.buffer.AdaptiveReceiveBufferSizePredictor;
import com.firefly.net.buffer.SocketReceiveBufferPool;
import com.firefly.net.buffer.SocketSendBufferPool;

public class Config {
	private int connectionTime = 1;
	private int latency = 2;
	private int bandwidth = 0;
	private int receiveBufferSize = 0;
	private int sendBufferSize = 0;
	private int backlog = 1024 * 16;
	private int timeout = 30000;
	private int port;
	private String host;

	private int handleThreads = 0;
	private int workerThreads = Runtime.getRuntime().availableProcessors() * 2;
	private int cleanupInterval = 256;
	private int writeSpinCount = 16;
	private int writeBufferHighWaterMark = 64 * 1024;
	private int writeBufferLowWaterMark = 32 * 1024;
	private String serverName = "firefly-server";
	private String clientName = "firefly-client";

	private ReceiveBufferSizePredictor receiveBufferSizePredictor = new AdaptiveReceiveBufferSizePredictor();
	private ReceiveBufferPool receiveBufferPool = new SocketReceiveBufferPool();
	private SendBufferPool sendBufferPool = new SocketSendBufferPool();

	private Decoder decoder;
	private Encoder encoder;
	private Handler handler;

	public SendBufferPool getSendBufferPool() {
		return sendBufferPool;
	}

	public void setSendBufferPool(SendBufferPool sendBufferPool) {
		this.sendBufferPool = sendBufferPool;
	}

	public int getWriteBufferHighWaterMark() {
		return writeBufferHighWaterMark;
	}

	public void setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
		this.writeBufferHighWaterMark = writeBufferHighWaterMark;
	}

	public int getWriteBufferLowWaterMark() {
		return writeBufferLowWaterMark;
	}

	public void setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
		this.writeBufferLowWaterMark = writeBufferLowWaterMark;
	}

	public ReceiveBufferSizePredictor getReceiveBufferSizePredictor() {
		return receiveBufferSizePredictor;
	}

	public void setReceiveBufferSizePredictor(
			ReceiveBufferSizePredictor receiveBufferSizePredictor) {
		this.receiveBufferSizePredictor = receiveBufferSizePredictor;
	}

	public ReceiveBufferPool getReceiveBufferPool() {
		return receiveBufferPool;
	}

	public void setReceiveBufferPool(ReceiveBufferPool receiveBufferPool) {
		this.receiveBufferPool = receiveBufferPool;
	}

	public int getWriteSpinCount() {
		return writeSpinCount;
	}

	public void setWriteSpinCount(int writeSpinCount) {
		this.writeSpinCount = writeSpinCount;
	}

	public int getCleanupInterval() {
		return cleanupInterval;
	}

	public void setCleanupInterval(int cleanupInterval) {
		this.cleanupInterval = cleanupInterval;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getReceiveBufferSize() {
		return receiveBufferSize;
	}

	public void setReceiveBufferSize(int receiveBufferSize) {
		this.receiveBufferSize = receiveBufferSize;
	}

	public int getSendBufferSize() {
		return sendBufferSize;
	}

	public void setSendBufferSize(int sendBufferSize) {
		this.sendBufferSize = sendBufferSize;
	}

	public Decoder getDecoder() {
		return decoder;
	}

	public void setDecoder(Decoder decoder) {
		this.decoder = decoder;
	}

	public Encoder getEncoder() {
		return encoder;
	}

	public void setEncoder(Encoder encoder) {
		this.encoder = encoder;
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

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

	@Override
	public String toString() {
		return "Config [handleThreads=" + handleThreads + ", workerThreads="
				+ workerThreads + ", connectionTime=" + connectionTime
				+ ", latency=" + latency + ", bandwidth=" + bandwidth
				+ ", receiveBufferSize=" + receiveBufferSize
				+ ", sendBufferSize=" + sendBufferSize + ", backlog=" + backlog
				+ ", timeout=" + timeout + ", port=" + port + ", host=" + host
				+ ", serverName=" + serverName + ", decoder=" + decoder
				+ ", encoder=" + encoder + ", handler=" + handler + "]";
	}

}
