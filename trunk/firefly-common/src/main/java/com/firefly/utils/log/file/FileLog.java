package com.firefly.utils.log.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import com.firefly.utils.StringUtils;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;
import com.firefly.utils.log.LogItem;
import com.firefly.utils.time.SafeSimpleDateFormat;

public class FileLog implements Log {
	private int level;
	private String path;
	private String name;
	private boolean console;

	void write(LogItem logItem) {
		String str = logItem.getLevel() + " " + logItem.getDate() + "\t"
				+ logItem.getContent();
		if(console)
			System.out.println(str);
		try {
			getBufferedWriter().append(str + CL).close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedWriter getBufferedWriter() throws IOException {
		File file = new File(path, name + "-"
				+ LogFactory.dayDateFormat.format(new Date()));
		if (!file.exists())
			file.createNewFile();
		return new BufferedWriter(new FileWriter(file, true));
	}

	public boolean isConsole() {
		return console;
	}

	public void setConsole(boolean console) {
		this.console = console;
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
