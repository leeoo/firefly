package test;

import java.net.URISyntaxException;
import com.firefly.net.Server;
import com.firefly.net.tcp.TcpServer;

public class FileTransferTcpServer {

    public static void main(String[] args) throws URISyntaxException {
//		System.out.println(SendFileHandler.class.getResource("/testFile.txt").toURI());
        Server server = new TcpServer(new StringLineDecoder(),
                new StringLineEncoder(), new SendFileHandler());
        server.start("10.147.22.162", 9900);
//        server.shutdown();
    }

}
