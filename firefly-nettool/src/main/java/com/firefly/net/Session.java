package com.firefly.net;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import com.firefly.net.buffer.SocketSendBufferPool.SendBuffer;

public interface Session {
	int CLOSE = 0;
	int OPEN = 1;
	ByteBuffer CLOSE_FLAG = ByteBuffer.allocate(0);

	void setAttribute(String key, Object value);

	Object getAttribute(String key);

	void removeAttribute(String key);

	void clearAttributes();

	void fireReceiveMessage(Object message);

	void encode(Object message);

	void write(ByteBuffer byteBuffer);

	int getInterestOps();

	int getRawInterestOps();

	int getSessionId();

	long getOpenTime();

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

	void close(boolean immediately);

	Queue<ByteBuffer> getWriteBuffer();

	void setCurrentWrite(ByteBuffer currentWrite);

	ByteBuffer getCurrentWrite();

	SendBuffer getCurrentWriteBuffer();

	void setCurrentWriteBuffer(SendBuffer currentWriteBuffer);

	int getState();

	void setState(int state);

	boolean isOpen();

	InetSocketAddress getLocalAddress();

	InetSocketAddress getRemoteAddress();
}
