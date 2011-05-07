package com.firefly.net;

import java.nio.ByteBuffer;

public interface Decoder {
	Object decode(ByteBuffer buf, Session session);
}
