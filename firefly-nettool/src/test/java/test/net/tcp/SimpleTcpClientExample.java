package test.net.tcp;

import com.firefly.net.Session;
import com.firefly.net.support.MessageReceiveCallBack;
import com.firefly.net.support.SimpleTcpClient;
import com.firefly.net.support.StringLineDecoder;
import com.firefly.net.support.StringLineEncoder;
import com.firefly.net.support.TcpConnection;

public class SimpleTcpClientExample {
	public static void main(String[] args) {
		final SimpleTcpClient client = new SimpleTcpClient("localhost", 9900,
				new StringLineDecoder(), new StringLineEncoder());
		TcpConnection c = client.connect();
		c.send("hello client 1", new MessageReceiveCallBack() {

			@Override
			public void messageRecieved(Session session, Object obj) {
				System.out.println("con1|" + obj.toString());

			}
		});

		c.send("test client 2", new MessageReceiveCallBack() {

			@Override
			public void messageRecieved(Session session, Object obj) {
				System.out.println("con1|" + obj.toString());

			}
		});

		c.send("test client 3", new MessageReceiveCallBack() {

			@Override
			public void messageRecieved(Session session, Object obj) {
				System.out.println("con1|" + obj.toString());
			}
		});

		c.send("quit", new MessageReceiveCallBack() {

			@Override
			public void messageRecieved(Session session, Object obj) {
				System.out.println("con1|" + obj.toString());
			}
		});

		TcpConnection c2 = client.connect();
		System.out.println("con2|" + c2.send("getfile"));
		c2.close(false);

		
	}
}
