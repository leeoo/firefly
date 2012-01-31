package com.firefly.net.support;

import java.nio.ByteBuffer;

import com.firefly.net.Decoder;
import com.firefly.net.Session;

public class StringLineDecoder implements Decoder {
	private static final byte LINE_LIMITOR = '\n';

	@Override
	public void decode(ByteBuffer buffer, Session session) throws Throwable {
		ByteBuffer now = buffer;
		ByteBuffer prev = (ByteBuffer) session.getAttribute("buff");

		if (prev != null) {
			session.removeAttribute("buff");
			now = (ByteBuffer) ByteBuffer
					.allocate(prev.remaining() + buffer.remaining()).put(prev)
					.put(buffer).flip();
		}

		int dataLen = now.position() + now.remaining();

		for (int i = now.position(), p = i; i < dataLen; i++) {
			if (now.get(i) == LINE_LIMITOR) {
				byte[] data = new byte[i - p + 1];
				now.get(data);
				String line = new String(data).trim();
				p = i + 1;
				session.fireReceiveMessage(line);
			}
		}

		if (now.hasRemaining()) {
			session.setAttribute("buff", now);
		}
	}

}
