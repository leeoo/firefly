package test.net.tcp;

import com.firefly.net.Session;
import com.firefly.net.support.MessageReceiveCallBack;
import com.firefly.net.support.SimpleTcpClient;
import com.firefly.net.support.StringLineDecoder;
import com.firefly.net.support.StringLineEncoder;
import com.firefly.net.support.TcpConnection;
import com.firefly.utils.log.LogFactory;

public class SimpleTcpClientExample {
	public static void main(String[] args) {
		SimpleTcpClient client = new SimpleTcpClient("localhost", 9900,
				new StringLineDecoder(), new StringLineEncoder());
		TcpConnection c = client.connect();
		c.send("hello client 1", new MessageReceiveCallBack() {

			@Override
			public void messageRecieved(Session session, Object obj) {
				System.out.println("con1|" + obj.toString());

			}
		});

		c.send("test", new MessageReceiveCallBack() {

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

		c.send("test 4", new MessageReceiveCallBack() {

			@Override
			public void messageRecieved(Session session, Object obj) {
				System.out.println("con1|" + obj.toString());
				session.close(false);
			}
		});

		c = client.connect();
		System.out.println("con2|" + c.send("getfile"));
		c.close(false);

		client.shutdown();
		LogFactory.getInstance().shutdown();
	}
}
