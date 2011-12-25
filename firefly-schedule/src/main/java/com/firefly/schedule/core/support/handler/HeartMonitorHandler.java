package com.firefly.schedule.core.support.handler;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.schedule.common.Config;
import com.firefly.schedule.core.Handler;
import com.firefly.schedule.core.Schedule;
import com.firefly.schedule.core.support.TaskDefinition;
import com.firefly.schedule.core.support.schedule.ScheduleInfo;
import com.firefly.schedule.db.DBManager;
import com.firefly.schedule.db.QueryHelper;
import com.firefly.schedule.utils.TimerUtil;

/**
 * 心跳监控
 * 
 * @author 须俊杰
 * @version 1.0 2011-10-20
 */
public class HeartMonitorHandler extends AbstractHandler {

	private final static Logger log = LoggerFactory.getLogger(HeartMonitorHandler.class);
	
	public HeartMonitorHandler(Config config) {
		super.setConfig(config);
	}
	
    /**
     * 处理流程：1. 定时往心跳表里更新当前调度器状态
     * <p>
     * 2. 检查所有调度器是否存活
     * <p>
     * 3. 若没有异常的调度器则本次心跳任务结束，否则重新计算任务分配
     */    
    @Override
    public void run() {
    	try {
        	log.debug("Start HeartMonitor!");
        	Schedule schedule = config.getSchedule();	// 当前调度器
            /* 发送心跳 */
            sendHeartbeat();
            
            /* 检查当前所有任务状态 */
            checkTaskClosed(schedule);
            
            // 调度器正在暂停
            if(Schedule.PAUSING == schedule.getStatus()){
            	// 检测任务完成状态
            	checkTaskFinish(schedule);
            	return;
            }
            
            /* 检测调度器心跳*/
            checkHeartbeat();
            
            log.debug("schedulename = "+ config.getScheduleName() + 
            		"; schedulenum = " + config.getScheduleNum() + 
            		"; seqno = " + config.getSeqno());
        } catch (Throwable t) {
            log.error("HeartMonitor error", t);
            DBManager.rollback();
        } finally {
            // 关闭数据库连接
            DBManager.closeConnection();
        }
    }
    
    /**
     * 发送心跳
     * @throws SQLException 当数据库操作有异常则抛出此异常
     */
    private void sendHeartbeat() throws SQLException{
		Schedule schedule = config.getSchedule();
		Object[] params = { config.getHostAddress(), schedule.getStatus() , config.getScheduleName()};
		QueryHelper.update("UPDATE jd_ofc_schedule SET updatetime = CONVERT(varchar, getdate(), 120 ) , wsurl = ? , status = ? "
				+ "WHERE schedulename = ?", params);
		DBManager.commit();
    }
    
    /**
     * 检测调度器心跳状态
     * <p>
     * 当发现调度器数量有变化，则重新进行任务分配
     * @throws SQLException 当数据库操作有异常则抛出此异常
     * @throws ParseException 当格式化日期时间有异常则抛出此异常
     */
    private void checkHeartbeat() throws SQLException, ParseException{
    	
    	boolean ready = true;						// 任务启动标志
    	Schedule schedule = config.getSchedule();	// 当前调度器对象
    	StringBuilder aliveSchedule = new StringBuilder(); 
    	
    	/* 获取所有的调度器 */
    	List<ScheduleInfo> scheduleList = QueryHelper.query(ScheduleInfo.class, 
                "SELECT id,schedulename,seqno,registertime,updatetime,status " + 
                			"FROM jd_ofc_schedule(NOLOCK) " + 
                			"ORDER BY id", new Object[]{});
    	
    	if (scheduleList == null || scheduleList.size() == 0){
    		log.error("The schedule heart error!");
    		return;
    	}
    	
    	/* 检测存活的调度器 */
    	String curTime = TimerUtil.getDbTime();		// 数据库当前时间
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	List<ScheduleInfo> aliveList = new ArrayList<ScheduleInfo>();
		for (ScheduleInfo scheduleInfo : scheduleList) {
			// 如果时间差小于超时时间，则判断为该调度器正常
			long timeDiff = sdf.parse(curTime).getTime() - sdf.parse(scheduleInfo.getUpdatetime()).getTime();
			if (timeDiff < config.getHeartTimeout()) {
				aliveList.add(scheduleInfo);
				aliveSchedule.append(scheduleInfo.getId());
				aliveSchedule.append(";");
				if (Schedule.PAUSED != scheduleInfo.getStatus() && ready) {
					ready = false;
				}
			}

		}
        log.debug("aliveList size : {"+aliveList.size()
        			+"} , start : {"+ready
        			+"} , scheduleNum : {"+config.getScheduleNum()
        			+"} , aliveSchedule = "+config.getAliveSchedule()
        			+" , curAliveSchedule = "+aliveSchedule.toString());
    	
		/* 如果存活调度器有变化，则暂停调度器任务处理 */
		if (!aliveSchedule.toString().equals(config.getAliveSchedule()) 
				&& Schedule.RUNNING == schedule.getStatus()) {
			abortTask(schedule);
		}

		/* 如果调度器就绪，则重新分配任务 */
		if (ready) {
			// 分配任务
			allocatingTask(aliveList);

			// 启动任务
			openAllTasks(schedule);
		}
		DBManager.commit();
    }
    
    /**
     * 检查当前所有任务状态都为已暂停后，将调度器的状态置为已暂停
     * <p>
     * 此方法用于在调度器执行暂停操作后
     * @param schedule
     */
    private void checkTaskFinish(Schedule schedule){
    	// 判断调度器中任务是否完成
		Map<String, TaskDefinition> taskMap = schedule.getTasks();
		for(Entry<String, TaskDefinition> entry : taskMap.entrySet()){
			TaskDefinition taskDefinition = entry.getValue();
			log.debug("CheckTaskFinish taskDefinition status ["+taskDefinition.getStatus()+"]");
			log.debug("CheckTaskFinish handler status ["+taskDefinition.getHandler().getStatus()+"]");
			if(TaskDefinition.CLOSED == taskDefinition.getStatus()){
				continue;
			}
			if(TaskDefinition.PAUSED == taskDefinition.getStatus() 
					&& Handler.FINISH != taskDefinition.getHandler().getStatus()){
				return;
			}
		}
		schedule.setStatus(Schedule.PAUSED); // 当所有任务都完成，则更改调度器状态为已暂停
    }
    
    /**
     * 检查当前所有任务状态
     * <p>
     * 当任务被执行close方法后，当前任务状态为Closing，
     * 此方法是在每次心跳时检测业务处理器是否已处理完任务，如果已处理完成，则将业务状态更改为closed
     * @param schedule
     */
    private void checkTaskClosed(Schedule schedule){
    	// 判断调度器中任务是否完成
		Map<String, TaskDefinition> taskMap = schedule.getTasks();
		for(Entry<String, TaskDefinition> entry : taskMap.entrySet()){
			TaskDefinition taskDefinition = entry.getValue();
			if(TaskDefinition.CLOSING == taskDefinition.getStatus() 
					&& Handler.FINISH == taskDefinition.getHandler().getStatus()){
				taskDefinition.setStatus(TaskDefinition.CLOSED);
			}
		}
    }
    
    /**
     * 暂停任务
     */
	private void abortTask(Schedule schedule) {
		schedule.pause();
		schedule.setStatus(Schedule.PAUSING); // 更改调度器状态为正在暂停
		log.info("Abort task [" + config.getScheduleName() + "]");
	}
    
    /**
     * 分配任务
     * @param aliveList 存活调度器列表
     * @throws SQLException 抛出数据库异常
     */
    private void allocatingTask(List<ScheduleInfo> aliveList) throws SQLException{
    	StringBuilder aliveSchedule = new StringBuilder();
    	for (int i = 0; i < aliveList.size(); ++i) {
    		ScheduleInfo scheduleInfo = aliveList.get(i);
            if (config.getScheduleName().equals(scheduleInfo.getSchedulename())) {
                // 将最新的序号同步到数据库
                Object[] arguments = { i, 
                		config.getScheduleName()};
                QueryHelper.update("UPDATE jd_ofc_schedule SET seqno = ? WHERE schedulename = ?", arguments);
                config.setSeqno(i);
            }
            aliveSchedule.append(scheduleInfo.getId());
            aliveSchedule.append(";");
        }
    	config.setAliveSchedule(aliveSchedule.toString());
        config.setScheduleNum(aliveList.size());
    }
    
    /**
     * 启动任务
     */
    private void openAllTasks(Schedule schedule){
    	schedule.setStatus(Schedule.RUNNING);
    	Map<String, TaskDefinition> taskMap = schedule.getTasks();
    	for(Entry<String, TaskDefinition> entry : taskMap.entrySet()){
			TaskDefinition taskDefinition = entry.getValue();
			if(TaskDefinition.PAUSED == taskDefinition.getStatus()){
    			schedule.open(entry.getKey());
    		}
		}
    	log.info("Allocate task ["+config.getScheduleName()+"]");
    }
}
