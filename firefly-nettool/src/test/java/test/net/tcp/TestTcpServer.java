package test.net.tcp;

import java.nio.ByteBuffer;
import com.firefly.net.Decoder;
import com.firefly.net.Encoder;
import com.firefly.net.Handler;
import com.firefly.net.Session;
import com.firefly.net.tcp.TcpServer;

public class TestTcpServer {
	private static final byte LINE_LIMITOR = '\n';

	public static void main(String[] args) {
		new TcpServer("localhost", 9900, new Decoder() {
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
					ByteBuffer succ = (ByteBuffer) ByteBuffer.allocate(
							now.remaining()).put(now).flip();
					session.setAttribute("buff", succ);
				}

				if(finished) {
					session.fireReceiveMessage(sline);
				}
			}
		}, new Encoder() {
			@Override
			public void encode(Object message, Session session) {
				String str = "server response: " + message + System.getProperty("line.separator");

				ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());
				session.write(byteBuffer);
			}
		}, new Handler() {

			@Override
			public void sessionOpened(Session session) {
				System.out.println("session open");
			}

			@Override
			public void sessionClosed(Session session) {
				System.out.println("session close");
			}

			@Override
			public void messageRecieved(Session session, Object message) {
				String str = (String) message;
				System.out.println("recive: " + str);
				session.encode(message);
			}

			@Override
			public void exceptionCaught(Session session, Throwable t) {
				System.out.println("session exception");
			}
		}).start();
	}
}
