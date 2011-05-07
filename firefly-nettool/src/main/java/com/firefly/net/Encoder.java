package com.firefly.net;

import java.nio.ByteBuffer;

public interface Encoder {
	ByteBuffer encode(Object message, Session session);
}
