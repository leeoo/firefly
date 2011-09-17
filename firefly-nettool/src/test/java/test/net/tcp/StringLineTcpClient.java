package test.net.tcp;

import com.firefly.net.Client;
import com.firefly.net.Session;
import com.firefly.net.Synchronizer;
import com.firefly.net.tcp.TcpClient;
import com.firefly.utils.log.LogFactory;

public class StringLineTcpClient {
	private String host;
	private int port;
	
	private Synchronizer<Session> synchronizer = new Synchronizer<Session>();
	private Client client;
	
	public StringLineTcpClient(String host, int port) {
		this.host = host;
		this.port = port;
		StringLineClientHandler handler = new StringLineClientHandler(synchronizer);
		client = new TcpClient(new StringLineDecoder(), new StringLineEncoder(), handler);
	}
	
	public Connection connect() {
		int id = client.connect(host, port);
		Connection ret = new Connection(synchronizer.get(id));
		return ret;
	}
	
	public void shutdown() {
		client.shutdown();
	}
	
	public static void main(String[] args) {
		StringLineTcpClient client = new StringLineTcpClient("localhost", 9900);
		Connection c = client.connect();
		c.send("hello client 1", new Callback(){

			@Override
			public void messageRecieved(Session session, Object obj) {
				System.out.println("con1|" + obj.toString());
				
			}});
		
		c.send("test", new Callback(){

			@Override
			public void messageRecieved(Session session, Object obj) {
				System.out.println("con1|" + obj.toString());
				
			}});
		
		
		c.send("test client 3", new Callback(){

			@Override
			public void messageRecieved(Session session, Object obj) {
				System.out.println("con1|" + obj.toString());
			}});
		
		
		c.send("test 4", new Callback(){

			@Override
			public void messageRecieved(Session session, Object obj) {
				System.out.println("con1|" + obj.toString());
				session.close(false);
			}});
		
		
		c = client.connect();
		System.out.println("con2|" + c.send("getfile"));
		c.close(false);
		
		client.shutdown();
		LogFactory.getInstance().shutdown();
	}
}
