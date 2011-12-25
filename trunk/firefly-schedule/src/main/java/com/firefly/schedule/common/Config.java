package com.firefly.schedule.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.firefly.schedule.core.Schedule;


/**
 * 提供项目所需的各种配置信息
 * 大部分配置信息来自文件config.properties
 * 
 * @author 须俊杰
 * @version 1.0 2011-7-15
 */
public class Config {
	/* 当前服务器WS地址 */
    private String hostAddress;
    /* 服务器名称 */
    private String scheduleName;
    /* 线程池线程数 */
    private Integer threadsNum = 200;
    /* 是否开启心跳 */
    private Boolean openHeart = false;
    /* 心跳频率 */
    private Integer heartBeat = 5000;
    /* 心跳超时时间 */
    private Integer heartTimeout = 20000;
    /* 存活调度器数量(用于取模，计算任务是否归当期调度器执行) */
    private int scheduleNum = 1;
    /* 所有调度器标识 */
    private String aliveSchedule;
    /* 当前调度器序号 */
    private int seqno;
    /* 当期调度器对象 */
    private Schedule schedule;
    
    public Config() {
    	try {
			InetAddress i = InetAddress.getLocalHost();
			scheduleName = i.getHostName();
			hostAddress = i.getHostAddress();
			aliveSchedule = "init";
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
    
    /**
     * 当前配置信息汇总，用于日志打印
     * @return 所有配置信息字符串
     */
    public String print() {
        return "Config{" +
                "hostaddress = " + hostAddress +
                ", schedulename = "+ scheduleName +
                ", threadsNum =" + threadsNum +
                ", openHeart = "+ openHeart +
                ", heartbeat = "+ heartBeat +
                ", hearttimeout = "+ heartTimeout +
                '}';
    }

	public String getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
	}

	public String getScheduleName() {
		return scheduleName;
	}

	public void setScheduleName(String scheduleName) {
		this.scheduleName = scheduleName;
	}

	public Integer getThreadsNum() {
		return threadsNum;
	}

	public void setThreadsNum(Integer threadsNum) {
		this.threadsNum = threadsNum;
	}
	
	public Boolean getOpenHeart() {
		return openHeart;
	}

	public void setOpenHeart(Boolean openHeart) {
		this.openHeart = openHeart;
	}

	public Integer getHeartBeat() {
		return heartBeat;
	}

	public void setHeartBeat(Integer heartBeat) {
		this.heartBeat = heartBeat;
	}

	public Integer getHeartTimeout() {
		return heartTimeout;
	}

	public void setHeartTimeout(Integer heartTimeout) {
		this.heartTimeout = heartTimeout;
	}

	public int getScheduleNum() {
		return scheduleNum;
	}

	public void setScheduleNum(int scheduleNum) {
		this.scheduleNum = scheduleNum;
	}

	public int getSeqno() {
		return seqno;
	}

	public void setSeqno(int seqno) {
		this.seqno = seqno;
	}

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	public String getAliveSchedule() {
		return aliveSchedule;
	}

	public void setAliveSchedule(String aliveSchedule) {
		this.aliveSchedule = aliveSchedule;
	}
}
