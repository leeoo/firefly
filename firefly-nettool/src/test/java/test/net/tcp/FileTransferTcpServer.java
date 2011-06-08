package test.net.tcp;

import java.net.URISyntaxException;

import com.firefly.net.tcp.TcpServer;

public class FileTransferTcpServer {

	/**
	 * @param args
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) throws URISyntaxException {
		System.out.println(SendFileHandler.class.getResource("/testFile.txt").toURI());

		new TcpServer("localhost", 9900, new StringLineDecoder(),
				new StringLineEncoder(), new SendFileHandler()).start();
	}

}
