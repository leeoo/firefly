package test.net.tcp;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.firefly.net.Session;
import com.firefly.net.SynchronousObject;

public class Connection {
	private Session session;
	private long timeout = 5000L;
	private BlockingQueue<MessageReceiveCallBack> queue = new ArrayBlockingQueue<MessageReceiveCallBack>(1024 * 8);
	
	public Connection(Session session) {
		this.session = session;
		this.session.setAttribute("#queue", queue);
	}
	
	public Object send(Object obj) {
		final SynchronousObject<Object> ret = new SynchronousObject<Object>();
		send(obj, new MessageReceiveCallBack(){

			@Override
			public void messageRecieved(Session session, Object obj) {
				ret.put(obj, timeout);
			}});
		
		return ret.get(timeout);
	}
	
	public void send(Object obj, MessageReceiveCallBack callback) {
		boolean offer = false;
		try {
			offer = queue.offer(callback, timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if(offer)
			session.encode(obj);
	}

	public int getId() {
		return session.getSessionId();
	}

	public void close(boolean b) {
		session.close(b);
	}
	
	public boolean isOpen() {
		return session.isOpen();
	}
	
	public Session getSession() {
		return session;
	}
}
