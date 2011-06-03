package test.net.tcp;

import com.firefly.net.tcp.TcpServer;

public class TestTcpServer {

	public static void main(String[] args) {
		new TcpServer("10.4.78.19", 9900, new StringLineDecoder(),
				new StringLineEncoder(), new StringLineHandler()).start();
	}
}
