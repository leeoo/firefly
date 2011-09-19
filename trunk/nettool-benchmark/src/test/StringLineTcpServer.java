package test;

import com.firefly.net.support.StringLineDecoder;
import com.firefly.net.support.StringLineEncoder;
import com.firefly.net.tcp.TcpServer;

public class StringLineTcpServer {

    public static void main(String[] args) {
    	System.setProperty("bind_host", "127.0.0.1");
    	System.setProperty("bind_port", "9900");
    	
    	String host = System.getProperty("bind_host");
    	int port = Integer.parseInt(System.getProperty("bind_port"));
        new TcpServer(new StringLineDecoder(),
                new StringLineEncoder(), new StringLineHandler()).start(host, port);
    }
}
