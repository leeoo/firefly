package com.firefly.schedule.core.support.schedule;

/**
 * 调度服务器信息
 * 
 * @author 须俊杰
 * @version 1.0 2011-10-18
 */
public class ScheduleInfo {
    // 唯一主键
    private Integer id;
    // 调度器唯一名称
    private String schedulename;
    // 序号
    private Integer seqno;
    // 注册时间
    private String registertime;
    // 更新时间
    private String updatetime;
    // 状态
    private Integer status;

    public Integer getId() {
        return id;
    }

    public String getSchedulename() {
        return schedulename;
    }

    public void setSchedulename(String schedulename) {
        this.schedulename = schedulename;
    }

    public Integer getSeqno() {
        return seqno;
    }

    public void setSeqno(Integer seqno) {
        this.seqno = seqno;
    }

    public String getRegistertime() {
        return registertime;
    }

    public void setRegistertime(String registertime) {
        this.registertime = registertime;
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
