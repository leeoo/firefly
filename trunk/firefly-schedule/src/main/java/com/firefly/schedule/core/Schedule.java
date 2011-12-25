package com.firefly.schedule.core;

import java.util.Map;

import com.firefly.schedule.common.Config;
import com.firefly.schedule.core.support.TaskDefinition;

/**
 * 调度器接口定义
 * 
 * @author 须俊杰
 * @version 1.0 2011-7-27
 */
public interface Schedule {
	/** 已停止 */
	public static final int STOPPED = 0;
	/** 运行中 */
	public static final int RUNNING = 1;
	/** 已暂停 */
	public static final int PAUSED = 2;
	/** 正在暂停 */
	public static final int PAUSING = 3;
	
	/**
	 * 启动调度器
	 */
	void start();
	
	/**
	 * 关闭调度器
	 */
	void shutdown();
	
	/**
	 * 设置调度器配置信息
	 * @param config 调度器配置信息
	 */
	void setConfig(Config config);
    /**
     * 启动单个任务
     * 
     * @param taskId 任务编号
     */
    void open(String taskId);

    /**
     * 关闭单个业务
     * 
     * @param taskId 任务编号
     */
    void close(String taskId);
    
    /**
     * 调度器暂停
     * <p>
     * 任务队列中正在处理的任务处理完之后不再处理新的任务
     */
    void pause();

    /**
     * 获取所有任务
     * 
     * @return 返回所有任务结果集
     */
    Map<String, TaskDefinition> getTasks();
    
    /**
     * 获取调度器状态
     * @return 返回调度器状态
     */
    int getStatus();
    
    /**
     * 设置调度器状态
     * @param status 调度器状态
     */
    void setStatus(int status);
}
