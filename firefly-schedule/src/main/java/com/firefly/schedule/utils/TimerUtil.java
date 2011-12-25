package com.firefly.schedule.utils;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.firefly.schedule.db.QueryHelper;

/**
 * 日期时间工具
 * 
 * @author 须俊杰
 * @version 1.0 2011-10-20
 */
public class TimerUtil {
	private static String dateStyle = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 获取当前时间
	 * 
	 * @return 返回yyyy-MM-dd HH:mm:ss类型时间
	 */
	public static String curTime() {
		DateFormat sdf = new SimpleDateFormat(dateStyle);
		return sdf.format(new Date());
	}

	/**
	 * 返回指定时间
	 * @param date 指定时间
	 * @return
	 */
	public static String curTime(Date date){
		DateFormat sdf = new SimpleDateFormat(dateStyle);
		return sdf.format(date);
	}
	
	/**
     * 获取当前数据库时间
     * @return 返回当前数据库时间
     * @throws SQLException 查询失败时抛出异常
     */
	public static String getDbTime() throws SQLException{
		return QueryHelper.read(String.class, "SELECT TOP 1 CONVERT(VARCHAR(19),GETDATE(),120) FROM jd_ofc_schedule(NOLOCK)", new Object[]{});
	}
}
