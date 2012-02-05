package com.firefly.server.http;

import com.firefly.net.Handler;
import com.firefly.net.Session;

public class HttpHandler implements Handler {

	@Override
	public void sessionOpened(Session session) throws Throwable {
		// TODO Auto-generated method stub

	}

	@Override
	public void sessionClosed(Session session) throws Throwable {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageRecieved(Session session, Object message)
			throws Throwable {
		// TODO 这里要保证request处理顺序，需要使用阻塞队列，队列的数量可以配置，然后按照sessionId进行取模

	}

	@Override
	public void exceptionCaught(Session session, Throwable t) throws Throwable {
		// TODO Auto-generated method stub

	}

}
