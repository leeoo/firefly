package com.firefly.utils.log.file;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.firefly.utils.VerifyUtils;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;
import com.firefly.utils.log.LogItem;
import com.firefly.utils.log.LogTask;

public class FileLogTask implements LogTask {
	private volatile boolean start;
	private BlockingQueue<LogItem> queue = new ArrayBlockingQueue<LogItem>(
			65535);
	private Thread thread = new Thread(this);

	@Override
	public void run() {
		while (start) {
			LogItem logItem = null;
			while ((logItem = queue.poll()) != null) {
				Log log = LogFactory.getInstance().getLog(logItem.getName());
				if (log instanceof FileLog) {
					FileLog fileLog = (FileLog) log;
					fileLog.write(logItem);
				}
			}
			try {
				Thread.sleep(1500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void start() {
		if (!start) {
			synchronized (this) {
				start = true;
				thread.start();
			}
		}
	}

	@Override
	public void shutdown() {
		if (start) {
			synchronized (this) {
				start = false;
			}
		}

	}

	@Override
	public void add(LogItem logItem) {
		if (!start)
			return;

		if (VerifyUtils.isEmpty(logItem.getName()))
			throw new IllegalArgumentException("log name is empty");

		try {
			queue.offer(logItem, 500L, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		}
	}
}
