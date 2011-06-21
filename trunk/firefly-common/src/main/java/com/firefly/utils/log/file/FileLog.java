package com.firefly.utils.log.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogException;
import com.firefly.utils.log.LogFactory;
import com.firefly.utils.log.LogItem;

public class FileLog implements Log {
	private int level;
	private String path;
	private String name;
	private BufferedWriter bufferedWriter;

	void write(LogItem logItem) {
		// TODO 
	}

	BufferedWriter getBufferedWriter() {
		File file = new File(path, name + "-"
				+ LogFactory.dayDateFormat.safeFormatDate(new Date()));
		if (bufferedWriter != null) {
			if (file.exists())
				return bufferedWriter;
			else {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				bufferedWriter = null;
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

	@Override
	public void trace(String str, Object... obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void trace(String str, Throwable throwable, Object... obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void debug(String str, String... obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void debug(String str, Throwable throwable, Object... obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void info(String str, String... obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void info(String str, Throwable throwable, Object... obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void warn(String str, String... obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void warn(String str, Throwable throwable, Object... obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void error(String str, String... obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void error(String str, Throwable throwable, Object... obj) {
		// TODO Auto-generated method stub

	}

}
