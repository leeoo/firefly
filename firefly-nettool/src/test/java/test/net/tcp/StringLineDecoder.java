package test.net.tcp;

import java.nio.ByteBuffer;

import com.firefly.net.Decoder;
import com.firefly.net.Session;

public class StringLineDecoder implements Decoder {
	private static final byte LINE_LIMITOR = '\n';

	@Override
	public void decode(ByteBuffer buffer, Session session) {
		ByteBuffer now = buffer;
		ByteBuffer prev = (ByteBuffer) session.getAttribute("buff");

		if (prev != null) {
			session.removeAttribute("buff");
			now = (ByteBuffer) ByteBuffer.allocate(
					prev.remaining() + buffer.remaining()).put(prev)
					.put(buffer).flip();
		}

		int p = 0;
		boolean finished = false;

		for (int i = 0; i < now.remaining(); i++) {
			if (now.get(i) == LINE_LIMITOR) {
				p = i;
				break;
			}
		}

		String sline = null;
		if (p != 0) {
			finished = true;
			byte[] data = new byte[p + 1];
			now.get(data);
			sline = new String(data, 0, p + 1).trim();
		}

		if (now.hasRemaining()) {
			ByteBuffer succ = (ByteBuffer) ByteBuffer.allocate(now.remaining())
					.put(now).flip();
			session.setAttribute("buff", succ);
		}

		if (finished) {
			session.fireReceiveMessage(sline);
		}

	}

}
