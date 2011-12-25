package com.firefly.schedule.core;

import com.firefly.schedule.common.Config;

/**
 * 调度器任务处理基本接口
 * 
 * @author 须俊杰
 * @version 1.0 2011-10-20
 */
public interface Handler extends Runnable {
	/** 已停止 */
	public static final int STOPPED = 0;
	/** 运行中 */
    public static final int RUNNING = 1;
    /** 完成 */
    public static final int FINISH = 2;
	
	/**
	 * 设置调度器配置信息
	 * @param config
	 */
	void setConfig(Config config);
	
	/**
	 * 获取处理器运行状态
	 * @return
	 */
	int getStatus();
}
