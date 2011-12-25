package com.firefly.schedule.core.support.handler;

import com.firefly.schedule.common.Config;
import com.firefly.schedule.core.Handler;

public abstract class AbstractHandler implements Handler {
	/* 调度器配置 */
	protected Config config;
	/* 处理器状态 */
	protected int status = STOPPED;
	/**
	 * 判断任务是否改当前调度器处理
	 * @param taskId 任务编号
	 * @return 如果是当前调度器处理的任务返回true，否则返回false
	 */
	protected boolean isMyTask(int taskId){
		if(taskId % config.getScheduleNum() == config.getSeqno())
			return true;
		
		return false;
	}
	
	@Override
	public void setConfig(Config config) {
		this.config = config;
	}

	@Override
	public int getStatus() {
		return status;
	}
}
