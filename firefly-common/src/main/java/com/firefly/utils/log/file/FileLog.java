package com.firefly.utils.log.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import com.firefly.utils.StringUtils;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogException;
import com.firefly.utils.log.LogFactory;
import com.firefly.utils.log.LogItem;
import com.firefly.utils.time.SafeSimpleDateFormat;

public class FileLog implements Log {
	private int level;
	private String path;
	private String name;
	private BufferedWriter bufferedWriter;

	public void close() {
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		bufferedWriter = null;
	}

	void write(LogItem logItem) {
		String str = logItem.getLevel() + " " + logItem.getDate() + " -| "
				+ logItem.getContent();
		System.out.println(str);
		try {
			getBufferedWriter().append(str + CL).flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedWriter getBufferedWriter() {
		File file = new File(path, name + "-"
				+ LogFactory.dayDateFormat.format(new Date()));
		if (bufferedWriter != null) {
			if (file.exists())
				return bufferedWriter;
			else {
				close();
			}
		}

		if (bufferedWriter == null) {
			try {
				if (!file.exists())
					file.createNewFile();

				bufferedWriter = new BufferedWriter(new FileWriter(file));
			} catch (IOException e) {
				throw new LogException("get bufferedWriter failure");
			}
		}
		return bufferedWriter;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private void add(String str, String level, Throwable throwable,
			Object... objs) {
		LogItem item = new LogItem();
		item.setLevel(level);
		item.setName(name);
		item.setDate(SafeSimpleDateFormat.defaultDateFormat.format(new Date()));
		String content = StringUtils.replace(str, objs);
		if (throwable != null) {
			content += CL;
			for (StackTraceElement ele : throwable.getStackTrace()) {
				content += ele + CL;
			}
		}
		item.setContent(content);
		LogFactory.getInstance().getLogTask().add(item);
	}

	@Override
	public void debug(String str, Object... objs) {
		if (level > Log.DEBUG)
			return;
		add(str, "DEBUG", null, objs);
	}

	@Override
	public void debug(String str, Throwable throwable, Object... objs) {
		if (level > Log.DEBUG)
			return;
		add(str, "DEBUG", throwable, objs);
	}

	@Override
	public void error(String str, Object... objs) {
		if (level > Log.ERROR)
			return;
		add(str, "ERROR", null, objs);
	}

	@Override
	public void error(String str, Throwable throwable, Object... objs) {
		if (level > Log.ERROR)
			return;
		add(str, "ERROR", throwable, objs);
	}

	@Override
	public void info(String str, Object... objs) {
		if (level > Log.INFO)
			return;
		add(str, "INFO", null, objs);
	}

	@Override
	public void info(String str, Throwable throwable, Object... objs) {
		if (level > Log.INFO)
			return;
		add(str, "INFO", throwable, objs);
	}

	@Override
	public void trace(String str, Object... objs) {
		if (level > Log.TRACE)
			return;
		add(str, "TRACE", null, objs);
	}

	@Override
	public void trace(String str, Throwable throwable, Object... objs) {
		if (level > Log.TRACE)
			return;
		add(str, "TRACE", null, objs);
	}

	@Override
	public void warn(String str, Object... objs) {
		if (level > Log.WARN)
			return;
		add(str, "WARN", null, objs);
	}

	@Override
	public void warn(String str, Throwable throwable, Object... objs) {
		if (level > Log.WARN)
			return;
		add(str, "WARN", throwable, objs);
	}

}
