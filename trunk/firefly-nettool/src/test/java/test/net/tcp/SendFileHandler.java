package test.net.tcp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;

import com.firefly.net.Handler;
import com.firefly.net.Session;
import com.firefly.net.buffer.FileRegion;

public class SendFileHandler implements Handler{

	@Override
	public void sessionOpened(Session session) {
		System.out.println("session open |" + session.getSessionId());
		System.out.println("local: " + session.getLocalAddress());
		System.out.println("remote: " + session.getRemoteAddress());
	}

	@Override
	public void sessionClosed(Session session) {
		System.out.println("session close|" + session.getSessionId());
	}

	@Override
	public void messageRecieved(Session session, Object message) {
		String str = (String) message;
		if (str.equals("quit")) {
			session.encode("bye!");
			session.close(false);
		} else if (str.equals("getfile")) {
			RandomAccessFile raf = null;
			File file = null;
			try {
				file = new File(SendFileHandler.class.getResource("/testFile.txt").toURI());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
	        try {
	            raf = new RandomAccessFile(file, "r");
	        } catch (FileNotFoundException fnfe) {
	        	fnfe.printStackTrace();
	        }
	        FileRegion fileRegion= null;
			try {
				fileRegion = new FileRegion(raf.getChannel(), 0, raf.length());
			} catch (IOException e) {
				e.printStackTrace();
			}
	        session.write(fileRegion);
		} else {
			System.out.println("recive: " + str);
			session.encode(message);
		}
	}

	@Override
	public void exceptionCaught(Session session, Throwable t) {
		System.out.println( t.getMessage() + "|" + session.getSessionId());
	}

}
