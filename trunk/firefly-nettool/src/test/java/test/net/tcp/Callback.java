package test.net.tcp;

import com.firefly.net.Session;

public interface Callback {
	void messageRecieved(Session session, Object obj);
}
