package com.firefly.utils.log.file;

import com.firefly.utils.log.LogItem;
import com.firefly.utils.log.LogTask;

public class FileLogProcessor implements LogTask {
	private volatile boolean start;
	private Thread thread = new Thread(this);

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void start() {
		if (!start) {
			synchronized (this) {
				thread.start();
				start = true;
			}
		}
	}

	@Override
	public void shutdown() {
		if (start) {
			synchronized (this) {
				thread.interrupt();
				start = false;
			}
		}

	}

	@Override
	public void add(LogItem logItem) {
		// TODO Auto-generated method stub

	}

}
