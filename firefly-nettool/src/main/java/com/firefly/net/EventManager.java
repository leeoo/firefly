package com.firefly.net;


public interface EventManager {

	public abstract void executeOpenTask(Session session);

	public abstract void executeReceiveTask(Session session, Object message);

	public abstract void executeCloseTask(Session session);

	public abstract void executeExceptionTask(Session session, Throwable t);

}