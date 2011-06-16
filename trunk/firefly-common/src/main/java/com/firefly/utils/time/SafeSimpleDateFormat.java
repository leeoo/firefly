package com.firefly.utils.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author alvinqiu 线程安全的时间日期格式化工具
 */
public class SafeSimpleDateFormat {

	public String datePattern = "yyyy-MM-dd HH:mm:ss";

	public SafeSimpleDateFormat() {

	}

	public SafeSimpleDateFormat(String datePattern) {
		this.datePattern = datePattern;
	}

	/**
	 * 线程安全转换 String -> Date
	 */
	public Date safeParseDate(String dateStr) {
		try {
			return getFormat().parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 线程安全格式化 Date -> String
	 */
	public String safeFormatDate(Date date) {
		return getFormat().format(date);
	}

	/**
	 * 借助ThreadLocal完成对每个线程第一次调用时初始化SimpleDateFormat对象
	 */
	private ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>() {
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat(datePattern);
		}
	};

	/**
	 * 获取当前线程中的安全SimpleDateFormat对象
	 */
	private DateFormat getFormat() {
		return (DateFormat) threadLocal.get();
	}

	public static void main(String[] args) {
		SafeSimpleDateFormat sdf = new SafeSimpleDateFormat();
		Calendar last = Calendar.getInstance();
		last.setTime(sdf.safeParseDate("2010-12-08 17:26:22"));
		Calendar now = Calendar.getInstance();
		System.out.println("last:\t" + last.get(Calendar.YEAR) + "\t"
				+ last.get(Calendar.MONTH));
		System.out.println("now:\t" + now.get(Calendar.YEAR) + "\t"
				+ now.get(Calendar.MONTH));
	}

}
