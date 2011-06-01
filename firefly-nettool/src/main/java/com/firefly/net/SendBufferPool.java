package com.firefly.net;

import java.nio.ByteBuffer;

import com.firefly.net.buffer.SocketSendBufferPool.SendBuffer;

public interface SendBufferPool {
	SendBuffer acquire(ByteBuffer src);
}
