package com.firefly.utils.log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import com.firefly.utils.StringUtils;
import com.firefly.utils.log.file.FileLog;
import com.firefly.utils.time.SafeSimpleDateFormat;

public class LogFactory {
	private Map<String, Log> logMap = new HashMap<String, Log>();
	private Map<String, Integer> levelMap = new HashMap<String, Integer>();
	public static final SafeSimpleDateFormat dayDateFormat = new SafeSimpleDateFormat("yyyy-MM-dd");

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
					.getResourceAsStream("/firefly-log.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Entry<Object, Object> entry : properties.entrySet()) {
			String name = (String) entry.getKey();
			String value = (String) entry.getValue();

			String[] strs = StringUtils.split(value, ',');
			if (strs.length < 2)
				throw new LogException("config format error");

			String path = strs[1];
			File file = new File(path);
			if(!file.exists()) {
				boolean mkdirRet = file.mkdir();
				if(!mkdirRet)
					throw new LogException("create dir " + path + " failure");
			}

			if (!file.isDirectory())
				throw new LogException(path + " is not directory");

			int level = levelMap.get(strs[0]);

			FileLog fileLog = new FileLog();
			fileLog.setName(name);
			fileLog.setLevel(level);
			fileLog.setPath(path);
			logMap.put(name, fileLog);
		}
	}

	public Log getLog(String name) {
		return logMap.get(name);
	}
}
