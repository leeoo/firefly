package com.firefly.utils.log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.firefly.utils.StringUtils;
import com.firefly.utils.log.file.FileLog;
import com.firefly.utils.log.file.FileLogTask;
import com.firefly.utils.time.SafeSimpleDateFormat;

public class LogFactory {
	private Map<String, Log> logMap = new HashMap<String, Log>();
	private Map<String, Integer> levelMap = new HashMap<String, Integer>();
	public static final SafeSimpleDateFormat dayDateFormat = new SafeSimpleDateFormat(
			"yyyy-MM-dd");
	private LogTask logTask = new FileLogTask();

	private static class Holder {
		private static LogFactory instance = new LogFactory();
	}

	public static LogFactory getInstance() {
		return Holder.instance;
	}

	public LogFactory() {
		levelMap.put("TRACE", Log.TRACE);
		levelMap.put("DEBUG", Log.DEBUG);
		levelMap.put("INFO", Log.INFO);
		levelMap.put("WARN", Log.WARN);
		levelMap.put("ERROR", Log.ERROR);

		Properties properties = new Properties();
		try {
			properties.load(LogFactory.class.getClassLoader()
					.getResourceAsStream("firefly-log.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String name = (String) entry.getKey();
			String value = (String) entry.getValue();
			System.out.println(name + "|" + value);

			String[] strs = StringUtils.split(value, ',');
			if (strs.length < 2)
				throw new LogException("config format error");

			String path = strs[1];
			File file = new File(path);
			if (!file.exists()) {
				boolean mkdirRet = file.mkdir();
				if (!mkdirRet)
					throw new LogException("create dir " + path + " failure");
			}

			if (!file.isDirectory())
				throw new LogException(path + " is not directory");

			int level = levelMap.get(strs[0]);

			FileLog fileLog = new FileLog();
			fileLog.setName(name);
			fileLog.setLevel(level);
			fileLog.setPath(path);
			if (strs.length > 2) {
				if ("console".equalsIgnoreCase(strs[2]))
					fileLog.setConsole(true);
			}

			logMap.put(name, fileLog);
		}
		logTask.start();
	}

	public Log getLog(String name) {
		return logMap.get(name);
	}

	public LogTask getLogTask() {
		return logTask;
	}

	public void shutdown() {
		logTask.shutdown();
	}

	public void start() {
		logTask.start();
	}

}
