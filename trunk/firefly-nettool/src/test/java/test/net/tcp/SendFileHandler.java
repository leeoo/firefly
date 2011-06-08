package test.net.tcp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.net.Handler;
import com.firefly.net.Session;
import com.firefly.net.buffer.FileRegion;

public class SendFileHandler implements Handler {
	private static Logger log = LoggerFactory.getLogger(SendFileHandler.class);

	@Override
	public void sessionOpened(Session session) {
		log.info("session open |" + session.getSessionId());
		log.info("local: " + session.getLocalAddress());
		log.info("remote: " + session.getRemoteAddress());
	}

	@Override
	public void sessionClosed(Session session) {
		log.info("session close|" + session.getSessionId());
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
				file = new File(SendFileHandler.class.getResource(
						"/testFile.txt").toURI());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			try {
				raf = new RandomAccessFile(file, "r");
			} catch (FileNotFoundException fnfe) {
				fnfe.printStackTrace();
			}
			FileRegion fileRegion = null;
			try {
				fileRegion = new FileRegion(raf.getChannel(), 0, raf.length());
			} catch (IOException e) {
				e.printStackTrace();
			}
			session.write(fileRegion);
		} else {
			log.debug("recive: " + str);
			session.encode(message);
		}
	}

	@Override
	public void exceptionCaught(Session session, Throwable t) {
		log.error(t.getMessage() + "|" + session.getSessionId());
	}

}
