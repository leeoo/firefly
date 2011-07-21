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
	private boolean consoleOutput;
	private boolean fileOutput;

	void write(LogItem logItem) {
		if (consoleOutput) {
			System.out.println(logItem.toString());
		}
		if (fileOutput) {
			BufferedWriter bufferedWriter = null;
			try {
				bufferedWriter = getBufferedWriter();
				bufferedWriter.append(logItem.toString() + CL);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(bufferedWriter != null)
					try {
						bufferedWriter.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}

	private BufferedWriter getBufferedWriter() throws IOException {
		File file = new File(path, name + "."
				+ LogFactory.dayDateFormat.format(new Date()) + ".txt");
		boolean ret = false;
		if (!file.exists()) {
			ret = file.createNewFile();
		} else {
			ret = true;
		}
		if (ret)
			return new BufferedWriter(new FileWriter(file, true));
		else
			return null;
	}

	public void setConsoleOutput(boolean consoleOutput) {
		this.consoleOutput = consoleOutput;
	}

	public void setFileOutput(boolean fileOutput) {
		this.fileOutput = fileOutput;
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
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append(CL);
			for (StackTraceElement ele : throwable.getStackTrace()) {
				strBuilder.append(ele).append(CL);
			}
			content += strBuilder.toString();
		}
		item.setContent(content);
		LogFactory.getInstance().getLogTask().add(item);
	}

	@Override
	public void trace(String str) {
		if (level > Log.TRACE)
			return;
		add(str, "TRACE", null, new Object[0]);
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
	public void debug(String str) {
		if (level > Log.DEBUG)
			return;
		add(str, "DEBUG", null, new Object[0]);
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
	public void info(String str) {
		if (level > Log.INFO)
			return;
		add(str, "INFO", null, new Object[0]);
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
	public void warn(String str) {
		if (level > Log.WARN)
			return;
		add(str, "WARN", null, new Object[0]);
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
	public void error(String str) {
		if (level > Log.ERROR)
			return;
		add(str, "ERROR", null, new Object[0]);
	}

}
