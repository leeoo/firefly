package test.net.tcp;

import com.firefly.net.Client;
import com.firefly.net.Session;
import com.firefly.net.tcp.TcpClient;

public class StringLineTcpClient {
	public static void main(String[] args) {
		StringLineClientHandler handler = new StringLineClientHandler();
		Client client = new TcpClient(new StringLineDecoder(),
				new StringLineEncoder(), handler);
		client.connect("localhost", 9900);

		Session session = handler.getSession();
		session.encode("hello client");
		String ret = (String) handler.getReceive();
		System.out.println("receive[" + ret + "]");

		session.encode("test2");
		ret = (String) handler.getReceive();
		System.out.println("receive[" + ret + "]");

		session.encode("quit");
		ret = (String) handler.getReceive();
		System.out.println("receive[" + ret + "]");

        client.connect("localhost", 9900);
        session = handler.getSession();
        session.encode("getfile");
        ret = (String) handler.getReceive();
		System.out.println("receive[" + ret + "]");

        session.close(false);
        client.shutdown();
	}
}
