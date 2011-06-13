package test.net.tcp;

import com.firefly.net.Client;
import com.firefly.net.Session;
import com.firefly.net.tcp.TcpClient;

public class StringLineTcpClient {
	public static void main(String[] args) {
		StringLineClientHandler handler = new StringLineClientHandler(2, 1024 * 4);
		Client client = new TcpClient(new StringLineDecoder(),
				new StringLineEncoder(), handler);
		int sessionId = client.connect("localhost", 9900);
        Session session = handler.getSession(sessionId);

        String message = "hello client";
        int revId = handler.getRevId(session.getSessionId(), message);
        System.out.println(revId);
		session.encode(message);
		String ret = handler.getReceive(revId);
		System.out.println("receive[" + ret + "]");

        message = "test2";
        revId = handler.getRevId(session.getSessionId(), message);
        System.out.println(revId);
		session.encode(message);
		ret = handler.getReceive(revId);
		System.out.println("receive[" + ret + "]");

		message = "quit";
        revId = handler.getRevId(session.getSessionId(), message);
        System.out.println(revId);
		session.encode(message);
		ret = handler.getReceive(revId);
		System.out.println("receive[" + ret + "]");


        sessionId = client.connect("localhost", 9900);
        session = handler.getSession(sessionId);

        message = "getfile";
        revId = handler.getRevId(session.getSessionId(), message);
        System.out.println(revId);
		session.encode(message);
        ret = handler.getReceive(revId);
		System.out.println("receive[" + ret + "]");

        session.close(false);
        client.shutdown();
	}
}
