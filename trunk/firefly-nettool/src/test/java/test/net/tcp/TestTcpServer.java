package test.net.tcp;

import java.nio.ByteBuffer;

import com.firefly.net.Decoder;
import com.firefly.net.Encoder;
import com.firefly.net.Handler;
import com.firefly.net.Session;
import com.firefly.net.tcp.TcpServer;

public class TestTcpServer {
	public static void main(String[] args) {
		new TcpServer("localhost", 9900, new Decoder() {
			@Override
			public void decode(ByteBuffer buf, Session session) {
				int remain = buf.remaining();
				byte[] s = new byte[remain];
				buf.get(s);
				String str = new String(s);
				session.fireReceiveMessage(str);
			}
		}, new Encoder() {
			@Override
			public void encode(Object message, Session session) {
				String str = (String)message;
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
				String str = (String)message;
				session.encode(message);
			}

			@Override
			public void exceptionCaught(Session session, Throwable t) {
				System.out.println("session exception");
			}
		}).start();
	}
}
