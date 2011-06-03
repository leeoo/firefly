package test.net.tcp;

import java.nio.ByteBuffer;

import com.firefly.net.Encoder;
import com.firefly.net.Session;

public class StringLineEncoder implements Encoder {

	@Override
	public void encode(Object message, Session session) {
		String str = message + System.getProperty("line.separator");

		ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes());
		session.write(byteBuffer);
	}

}
