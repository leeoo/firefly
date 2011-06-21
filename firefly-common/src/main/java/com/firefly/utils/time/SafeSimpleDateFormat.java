package com.firefly.utils.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.firefly.utils.VerifyUtils;

/**
 * @author alvinqiu 线程安全的时间日期格式化工具
 */
public class SafeSimpleDateFormat {

	private ThreadLocal<SimpleDateFormat> threadLocal;

	public SafeSimpleDateFormat() {
		this("");
	}

	public SafeSimpleDateFormat(final SimpleDateFormat sdf) {
		if(sdf == null)
			throw new IllegalArgumentException("SimpleDateFormat argument is null");
		this.threadLocal = new ThreadLocal<SimpleDateFormat>() {
			protected SimpleDateFormat initialValue() {
				return sdf;
			}
		};
	}

	public SafeSimpleDateFormat(String datePattern) {
		final String p = VerifyUtils.isEmpty(datePattern) ? "yyyy-MM-dd HH:mm:ss"
				: datePattern;
		this.threadLocal = new ThreadLocal<SimpleDateFormat>() {
			protected SimpleDateFormat initialValue() {
				return new SimpleDateFormat(p);
			}
		};
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
