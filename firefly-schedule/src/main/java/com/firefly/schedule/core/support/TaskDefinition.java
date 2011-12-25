package com.firefly.schedule.core.support;

import java.util.concurrent.ScheduledFuture;

import com.firefly.schedule.core.Handler;

/**
 * 调度器任务定义接口
 * <p>
 * 此接口用来定义一个规范，适用这个规范的任务都能被调度器处理
 * 
 * @author 须俊杰
 * @version 1.0 2011-11-14
 */
public interface TaskDefinition {
	/** 关闭 */
    public static final int CLOSED = 0;
	/** 开启 */
    public static final int OPENED = 1;
    /** 暂停 */
    public static final int PAUSED = 2;
    /** 关闭中 */
    public static final int CLOSING = 3;
    /**无此任务*/
    public static final int UNAVAILABLE = -1;
    /**
     * 任务唯一标识
     * @return
     */
    String getId();
    
    /**
     * 返回当前任务关闭状态
     * @return 返回true为关闭，false为开启
     */
    boolean isClosed();
    
    /**
     * 获取任务执行频率
     * @return 返回任务频率
     */
    Long getFrequency();
    
    /**
     * 修改任务状态
     */
    void setStatus(int status);
    
    /**
     * 获取任务状态
     */
    int getStatus();
    
    /**
     * 获取业务处理器
     * @return
     */
    Handler getHandler();
    
    /**
     * 持有当前任务futrue
     * @param future
     */
    void setFuture(ScheduledFuture<?> future);
    
    /**
     * 获取当前任务
     * @return
     */
    ScheduledFuture<?> getFuture();
}
