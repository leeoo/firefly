package com.firefly.net.tcp;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import com.firefly.net.Config;
import com.firefly.net.EventType;
import com.firefly.net.Session;

public class TcpSession implements Session {
	private final int sessionId;
	private final SocketChannel socketChannel;
	private long openTime;
	private final TcpWorker worker;
	private final Config config;
	private final Map<String, Object> map = new HashMap<String, Object>();
	private final Runnable writeTask = new WriteTask();
	private final AtomicInteger writeBufferSize = new AtomicInteger();
	private final AtomicInteger highWaterMarkCounter = new AtomicInteger();
	private final AtomicBoolean writeTaskInTaskQueue = new AtomicBoolean();
	private volatile int interestOps = SelectionKey.OP_READ;
	private boolean inWriteNowLoop;
	private boolean writeSuspended;
	private final Object interestOpsLock = new Object();
	private final Object writeLock = new Object();

	public TcpSession(int sessionId, TcpWorker worker, Config config,
			long openTime, SocketChannel socketChannel) {
		super();
		this.sessionId = sessionId;
		this.worker = worker;
		this.config = config;
		this.openTime = openTime;
		this.socketChannel = socketChannel;
	}

	public Runnable getWriteTask() {
		return writeTask;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public Object getInterestOpsLock() {
		return interestOpsLock;
	}

	public Object getWriteLock() {
		return writeLock;
	}

	public boolean isInWriteNowLoop() {
		return inWriteNowLoop;
	}

	public void setInWriteNowLoop(boolean inWriteNowLoop) {
		this.inWriteNowLoop = inWriteNowLoop;
	}

	public boolean isWriteSuspended() {
		return writeSuspended;
	}

	public void setWriteSuspended(boolean writeSuspended) {
		this.writeSuspended = writeSuspended;
	}

	public AtomicInteger getWriteBufferSize() {
		return writeBufferSize;
	}

	public AtomicInteger getHighWaterMarkCounter() {
		return highWaterMarkCounter;
	}

	public long getOpenTime() {
		return openTime;
	}

	public int getSessionId() {
		return sessionId;
	}

	@Override
	public void setAttribute(String key, Object value) {
		map.put(key, value);
	}

	@Override
	public Object getAttribute(String key) {
		return map.get(key);
	}

	@Override
	public void removeAttribute(String key) {
		map.remove(key);
	}

	@Override
	public void clearAttributes() {
		map.clear();
	}

	@Override
	public void fireReceiveMessage(Object message) {
		worker.fire(EventType.RECEIVE, this, message, null);
	}

	@Override
	public void encode(Object message) {
		config.getEncoder().encode(message, this);
	}

	@Override
	public void write(ByteBuffer byteBuffer) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getInterestOps() {
		int interestOps = getRawInterestOps();
		int writeBufferSize = this.writeBufferSize.get();
		if (writeBufferSize != 0) {
			if (highWaterMarkCounter.get() > 0) {
				int lowWaterMark = config.getWriteBufferLowWaterMark();
				if (writeBufferSize >= lowWaterMark) {
					interestOps |= SelectionKey.OP_WRITE;
				} else {
					interestOps &= ~SelectionKey.OP_WRITE;
				}
			} else {
				int highWaterMark = config.getWriteBufferHighWaterMark();
				if (writeBufferSize >= highWaterMark) {
					interestOps |= SelectionKey.OP_WRITE;
				} else {
					interestOps &= ~SelectionKey.OP_WRITE;
				}
			}
		} else {
			interestOps &= ~SelectionKey.OP_WRITE;
		}
		return interestOps;
	}

	@Override
	public int getRawInterestOps() {
		return interestOps;
	}

	@Override
	public void setInterestOpsNow(int interestOps) {
		this.interestOps = interestOps;
	}

	private final class WriteTask implements Runnable {

		WriteTask() {
			super();
		}

		@Override
		public void run() {
			writeTaskInTaskQueue.set(false);
			worker.writeFromTaskLoop(TcpSession.this);
		}
	}

}
