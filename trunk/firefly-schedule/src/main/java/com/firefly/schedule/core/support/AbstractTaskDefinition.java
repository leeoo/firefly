package com.firefly.schedule.core.support;

import java.util.concurrent.ScheduledFuture;

public abstract class AbstractTaskDefinition implements TaskDefinition {
	
	/* 任务当前运行状态 */
	protected int status = CLOSED;
	
	/* 当前正在运行的任务 */
	protected ScheduledFuture<?> future;

	@Override
	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}

	@Override
	public ScheduledFuture<?> getFuture() {
		return this.future;
	}
	
	@Override
	public void setStatus(int status) {
		this.status = status;
	}
	
	@Override
	public int getStatus() {
		return this.status;
	}
	
	@Override
	public boolean isClosed() {
		if(this.status == TaskDefinition.CLOSED || this.status == TaskDefinition.PAUSED)
			return true;
		return false;
	}
}
