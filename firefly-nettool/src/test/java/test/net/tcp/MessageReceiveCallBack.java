package test.net.tcp;

import com.firefly.net.Session;

public interface MessageReceiveCallBack {
	void messageRecieved(Session session, Object obj);
}
