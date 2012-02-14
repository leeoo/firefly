package test.http;

import java.io.File;

import com.firefly.server.ServerBootstrap;

public class ServerDemo {

	public static void main(String[] args) throws Throwable {
		String serverHome = new File(ServerBootstrap.class.getResource("/page")
				.toURI()).getAbsolutePath();
		ServerBootstrap.start(serverHome, "localhost", 6655);
	}

}
