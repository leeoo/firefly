package com.firefly.net;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.firefly.net.buffer.SocketSendBufferPool.SendBuffer;

public interface Session {
	void setAttribute(String key, Object value);

	Object getAttribute(String key);

	void removeAttribute(String key);

	void clearAttributes();

	void fireReceiveMessage(Object message);

	void encode(Object message);

	void write(ByteBuffer byteBuffer);

//	int getInterestOps();

	int getRawInterestOps();

	int getSessionId();

	long getOpenTime();

	AtomicInteger getHighWaterMarkCounter();

	AtomicInteger getWriteBufferSize();

	void setWriteSuspended(boolean writeSuspended);

	boolean isWriteSuspended();

	void setInWriteNowLoop(boolean inWriteNowLoop);

	boolean isInWriteNowLoop();

	void setInterestOpsNow(int interestOps);

	Object getInterestOpsLock();

	Object getWriteLock();

	SelectionKey getSelectionKey();

	Runnable getWriteTask();

	AtomicBoolean getWriteTaskInTaskQueue();

	void close();

	Queue<ByteBuffer> getWriteBuffer();

	void setCurrentWrite(ByteBuffer currentWrite);

	ByteBuffer getCurrentWrite();

	SendBuffer getCurrentWriteBuffer();

	void setCurrentWriteBuffer(SendBuffer currentWriteBuffer);
}
