package test.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerDemoDebug {

	public static void main(String[] args) throws Throwable {
		Socket client = new Socket("localhost", 6655);
		OutputStream out = client.getOutputStream();
		out.write("GET /app/index HTTP/1.1\r\nHost: localhost:6655\r\n\r\n"
				.getBytes("UTF-8"));
		out.flush();
		InputStream in = client.getInputStream();
		byte[] data = new byte[16 * 1024];
		in.read(data);
		System.out.println(new String(data, "UTF-8").trim());
	}

}
