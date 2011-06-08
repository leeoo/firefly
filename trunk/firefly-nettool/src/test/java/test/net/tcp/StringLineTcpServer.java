package test.net.tcp;

import com.firefly.net.tcp.TcpServer;

public class StringLineTcpServer {

	public static void main(String[] args) {
		new TcpServer("localhost", 9900, new StringLineDecoder(),
				new StringLineEncoder(), new StringLineHandler()).start();
	}
}
