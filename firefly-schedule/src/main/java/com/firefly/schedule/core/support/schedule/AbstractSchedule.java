package com.firefly.schedule.core.support.schedule;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.schedule.common.Config;
import com.firefly.schedule.core.Handler;
import com.firefly.schedule.core.Schedule;
import com.firefly.schedule.core.support.TaskDefinition;
import com.firefly.schedule.core.support.handler.HeartMonitorHandler;
import com.firefly.schedule.db.DBManager;
import com.firefly.schedule.db.QueryHelper;
import com.firefly.schedule.utils.TimerUtil;

public abstract class AbstractSchedule implements Schedule{
	
	private final static Logger log = LoggerFactory.getLogger(AbstractSchedule.class);
	/** 调度器参数 */
    protected Config config;
    /** 所有任务 */
    protected Map<String, TaskDefinition> taskMap = new HashMap<String, TaskDefinition>();
    /** 定时调度线程池 */
    protected ScheduledExecutorService service;
    /** 调度器状态 */
    private int status = STOPPED;
    
    /**
     * 调度器初始化
     * <p>
     * 初始化流程：1. 初始化定时调度线程池
     * <p>
     * 2. 加入心跳监控
     * <p>
     * 3. 开启心跳监控
     */
    @Override
    public void start() {
    	if(config == null)
    		config = new Config();
    	
        /* 所有配置信息 */
    	log.info(config.print());

        try {
            if (taskMap != null && taskMap.size() > 0)
                return;

            // 初始化线程池
            service = Executors.newScheduledThreadPool(config.getThreadsNum());

            // 任务注册
            List<TaskDefinition> tasks = register();
            for(TaskDefinition taskDefinition : tasks){
            	if(taskMap.containsKey(taskDefinition.getId())){
            		log.error("The task key exist!");
            		break;
            	}
            	taskMap.put(taskDefinition.getId(), taskDefinition);
            }
            
            config.setSchedule(this);
            
            if(config.getOpenHeart()){
            	// 加入心跳监控
                joinHeartMonitor();

                // 开启心跳
                service.scheduleWithFixedDelay(new HeartMonitorHandler(config), 0, config.getHeartBeat(), TimeUnit.MILLISECONDS);
            }
            
            DBManager.commit();
        } catch (Exception e) {
            DBManager.rollback();
            log.error("Failed to initialize schedule", e);
        } finally {
            // 关闭数据库连接
            DBManager.closeConnection();
            if(!config.getOpenHeart())
            	this.status = RUNNING;
            else
            	this.status = PAUSED;
        }
    }
    
    @Override
    public void shutdown() {
    	service.shutdown();
    	this.status = STOPPED;
    }
    
    @Override
    public synchronized void open(String taskId) {
        try {
        	TaskDefinition taskDefinition = taskMap.get(taskId);
            /* 查看任务是否已启动 */
            if (Schedule.RUNNING == this.status 
            		&& (taskDefinition == null || !taskDefinition.isClosed())){
            	return;
            }
            log.debug("openHeart = "+config.getOpenHeart()+" , schedule status = "+this.status);
            
            /*
             * 如果调度器使用心跳监控，则任务必须在所有调度器等待就绪后才能开启
             * 
             * 所以将状态改为已暂停
             */
			if (config.getOpenHeart() 
					&& this.status != RUNNING 
					&& TaskDefinition.CLOSED != taskDefinition.getStatus()) {
				taskDefinition.setStatus(TaskDefinition.PAUSED);
				return;
			}
            
            /* 启动任务 */
            Handler handler = taskDefinition.getHandler();
            if(handler == null){
            	log.error("There is no handler to process!");
            	return;
            }
            handler.setConfig(config);
            ScheduledFuture<?> future = service.scheduleWithFixedDelay(handler, 0, taskDefinition.getFrequency(), TimeUnit.MILLISECONDS);
            taskDefinition.setFuture(future);
            taskDefinition.setStatus(TaskDefinition.OPENED); // 修改业务状态为运行中
            log.info("Task {"+taskId+"} is opened!");
        } catch (Exception e) {
            log.error("Failed to open task", e);
        }
    }
    
    @Override
    public synchronized void close(String taskId) {
        try {
            // 关闭业务
            TaskDefinition taskDefinition = taskMap.get(taskId);
            
            if(taskDefinition == null || taskDefinition.isClosed())
            	return;
            
            ScheduledFuture<?> future = taskDefinition.getFuture();
            if (future != null){
                future.cancel(false);
            }
            taskDefinition.setStatus(TaskDefinition.CLOSING); // 修改业务状态为关闭中
            log.info("Task {"+taskId+"} is closed!");
        } catch (Exception e) {
            log.error("Failed to close task", e);
        }
    }

    @Override
	public synchronized void pause() {
		try {
			// 暂停业务
			for (String key : taskMap.keySet()) {
				TaskDefinition taskDefinition = taskMap.get(key);
				if (taskDefinition.isClosed())
					continue;
				ScheduledFuture<?> future = taskDefinition.getFuture();
				if (future != null)
					future.cancel(false);
				taskDefinition.setStatus(TaskDefinition.PAUSED); // 修改业务状态为暂停
			}
			log.info(config.getScheduleName() + " is paused!");
		} catch (Exception e) {
			log.error("Failed to pause task", e);
		} 
	}
    
    /**
     * 加入心跳监控
     * 
     * @throws SQLException
     */
    private void joinHeartMonitor() throws SQLException {
    	/* 判断是否已加入心跳监控 */
        Object[] queryParams = { config.getScheduleName()};
        ScheduleInfo scheduleInfo = QueryHelper.read(ScheduleInfo.class, 
                "SELECT id,schedulename,seqno,registertime,updatetime " + 
                		"FROM jd_ofc_schedule(NOLOCK) " + 
                		"WHERE schedulename = ?", queryParams);
        if (scheduleInfo != null){
        	config.setSeqno(scheduleInfo.getSeqno());
        	return;
        }
        
        /* 查询当前最大的序号 */
        Integer maxSeqno = QueryHelper.read(Integer.class, "SELECT Max(seqno) FROM jd_ofc_schedule(NOLOCK)", new Object[]{});
        int seqno = (maxSeqno == null) ? 0 : maxSeqno + 1;

        String curtime = TimerUtil.getDbTime();

        /* 将当前调度器加入监控 */
        Object[] arguments = { config.getScheduleName(), 
        		seqno, 
        		curtime, 
        		curtime };
        QueryHelper.update("INSERT INTO jd_ofc_schedule (schedulename,seqno,registertime,updatetime) VALUES (?,?,?,?)", arguments);
        config.setSeqno(seqno);
    }
    
    @Override
	public Map<String, TaskDefinition> getTasks() {
		return this.taskMap;
	}
       
    @Override
	public void setConfig(Config config) {
		this.config = config;
	}

    @Override
	public int getStatus() {
		return status;
	}
    
    @Override
	public void setStatus(int status) {
		this.status = status;
	}

	/**
     * 任务注册
     * @return 返回需要处理的任务集合
     * @throws SQLException
     */
    abstract protected List<TaskDefinition> register() throws SQLException;
}
