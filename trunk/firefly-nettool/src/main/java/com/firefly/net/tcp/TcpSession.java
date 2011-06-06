package com.firefly.net.tcp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.firefly.net.Config;
import com.firefly.net.Session;
import com.firefly.net.ThreadLocalBoolean;
import com.firefly.net.buffer.SocketSendBufferPool.SendBuffer;

public class TcpSession implements Session {
	private static Logger log = LoggerFactory.getLogger(TcpSession.class);
	private final int sessionId;
	private final SelectionKey selectionKey;
	private long openTime;
	private final TcpWorker worker;
	private final Config config;
	private final Map<String, Object> map = new HashMap<String, Object>();
	private final Runnable writeTask = new WriteTask();
	private final AtomicInteger writeBufferSize = new AtomicInteger();
	private final AtomicInteger highWaterMarkCounter = new AtomicInteger();
	private final AtomicBoolean writeTaskInTaskQueue = new AtomicBoolean();
	private InetSocketAddress localAddress;
	private volatile InetSocketAddress remoteAddress;
	private volatile int interestOps = SelectionKey.OP_READ;
	private boolean inWriteNowLoop;
	private boolean writeSuspended;
	private final Object interestOpsLock = new Object();
	private final Object writeLock = new Object();
	private final Queue<ByteBuffer> writeBuffer = new WriteRequestQueue();
	private ByteBuffer currentWrite;
	private SendBuffer currentWriteBuffer;
	private volatile int state;

	public TcpSession(int sessionId, TcpWorker worker, Config config,
			long openTime, SelectionKey selectionKey) {
		super();
		this.sessionId = sessionId;
		this.worker = worker;
		this.config = config;
		this.openTime = openTime;
		this.selectionKey = selectionKey;
		state = OPEN;
	}

	public InetSocketAddress getLocalAddress() {
		if (localAddress == null) {
			SocketChannel socket = (SocketChannel) selectionKey.channel();
			try {
				localAddress = (InetSocketAddress) socket.socket()
						.getLocalSocketAddress();

			} catch (Throwable t) {
				log.error("get localAddress error", t);
			}
		}
		return localAddress;
	}

	public InetSocketAddress getRemoteAddress() {
		if (remoteAddress == null) {
			SocketChannel socket = (SocketChannel) selectionKey.channel();
			try {
				remoteAddress = (InetSocketAddress) socket.socket()
						.getRemoteSocketAddress();
			} catch (Throwable t) {
				log.error("get remoteAddress error", t);
			}
		}
		return remoteAddress;
	}

	AtomicBoolean getWriteTaskInTaskQueue() {
		return writeTaskInTaskQueue;
	}

	public int getState() {
		return state;
	}

	void setState(int state) {
		this.state = state;
	}

	@Override
	public boolean isOpen() {
		return state > 0;
	}

	SendBuffer getCurrentWriteBuffer() {
		return currentWriteBuffer;
	}

	void setCurrentWriteBuffer(SendBuffer currentWriteBuffer) {
		this.currentWriteBuffer = currentWriteBuffer;
	}

	void setCurrentWrite(ByteBuffer currentWrite) {
		this.currentWrite = currentWrite;
	}

	ByteBuffer getCurrentWrite() {
		return currentWrite;
	}

	Queue<ByteBuffer> getWriteBuffer() {
		return writeBuffer;
	}

	Runnable getWriteTask() {
		return writeTask;
	}

	SelectionKey getSelectionKey() {
		return selectionKey;
	}

	Object getInterestOpsLock() {
		return interestOpsLock;
	}

	Object getWriteLock() {
		return writeLock;
	}

	boolean isInWriteNowLoop() {
		return inWriteNowLoop;
	}

	void setInWriteNowLoop(boolean inWriteNowLoop) {
		this.inWriteNowLoop = inWriteNowLoop;
	}

	boolean isWriteSuspended() {
		return writeSuspended;
	}

	void setWriteSuspended(boolean writeSuspended) {
		this.writeSuspended = writeSuspended;
	}

	AtomicInteger getWriteBufferSize() {
		return writeBufferSize;
	}

	AtomicInteger getHighWaterMarkCounter() {
		return highWaterMarkCounter;
	}

	@Override
	public long getOpenTime() {
		return openTime;
	}

	@Override
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
		worker.getEventManager().executeReceiveTask(this, message);
	}

	@Override
	public void encode(Object message) {
		config.getEncoder().encode(message, this);
	}

	@Override
	public void write(ByteBuffer byteBuffer) {
		boolean offered = writeBuffer.offer(byteBuffer);
		assert offered;
		worker.writeFromUserCode(this);
	}


	int getRawInterestOps() {
		return interestOps;
	}

	void setInterestOpsNow(int interestOps) {
		this.interestOps = interestOps;
	}

	@Override
	public void close(boolean immediately) {
		if(immediately)
			worker.close(selectionKey);
		else
			write(CLOSE_FLAG);
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

	private final class WriteRequestQueue extends
			ConcurrentLinkedQueue<ByteBuffer> {
		private static final long serialVersionUID = -2493148252918843163L;
		private final ThreadLocalBoolean notifying = new ThreadLocalBoolean();

		private WriteRequestQueue() {
			super();
		}

		@Override
		public boolean offer(ByteBuffer byteBuffer) {
			boolean success = super.offer(byteBuffer);
			assert success;

			int messageSize = byteBuffer.remaining();
			int newWriteBufferSize = writeBufferSize.addAndGet(messageSize);
			int highWaterMark = config.getWriteBufferHighWaterMark();

			if (newWriteBufferSize >= highWaterMark) {
				if (newWriteBufferSize - messageSize < highWaterMark) {
					highWaterMarkCounter.incrementAndGet();
					if (!notifying.get()) {
						notifying.set(Boolean.TRUE);
						worker.setInterestOps(TcpSession.this,
								SelectionKey.OP_READ);
						notifying.set(Boolean.FALSE);
					}
				}
			}
			return true;
		}

		@Override
		public ByteBuffer poll() {
			ByteBuffer byteBuffer = super.poll();
			if (byteBuffer != null) {
				int messageSize = byteBuffer.remaining();
				int newWriteBufferSize = writeBufferSize
						.addAndGet(-messageSize);
				int lowWaterMark = config.getWriteBufferLowWaterMark();

				if (newWriteBufferSize == 0
						|| newWriteBufferSize < lowWaterMark) {
					if (newWriteBufferSize + messageSize >= lowWaterMark) {
						highWaterMarkCounter.decrementAndGet();
						if (!notifying.get()) {
							notifying.set(Boolean.TRUE);
							worker.setInterestOps(TcpSession.this,
									SelectionKey.OP_READ);
							notifying.set(Boolean.FALSE);
						}
					}
				}
			}
			return byteBuffer;
		}
	}

	@Override
	public int getInterestOps() {
		if (!isOpen()) {
			return SelectionKey.OP_WRITE;
		}

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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + sessionId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TcpSession other = (TcpSession) obj;
		if (sessionId != other.sessionId)
			return false;
		return true;
	}

}
