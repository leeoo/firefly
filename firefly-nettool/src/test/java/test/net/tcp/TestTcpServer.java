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

			}
		}, new Encoder() {
			@Override
			public void encode(Object message, Session session) {

			}
		}, new Handler() {

			@Override
			public void sessionOpened(Session session) {

			}

			@Override
			public void sessionClosed(Session session) {

			}

			@Override
			public void messageRecieved(Session session, Object message) {

			}

			@Override
			public void exceptionCaught(Session session) {

			}
		}).start();
	}
}
