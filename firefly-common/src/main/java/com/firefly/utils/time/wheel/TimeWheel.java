package com.firefly.utils.time.wheel;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TimeWheel {
	private Config config = new Config();
	private ConcurrentLinkedQueue<TimerTask>[] timerSlots;
	private volatile int currentSlot = 0;
	private volatile boolean start;

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	/**
	 * 增加一个触发任务
	 * 
	 * @param delay
	 *            触发延时时间
	 * @param run
	 *            任务处理
	 */
	public void add(long delay, Runnable run) {
		final int maxTimers = config.getMaxTimers();
		final int curSlot = currentSlot;
		final int ticks = delay > config.getInterval() ? (int) (delay / config
				.getInterval()) : 1; // 计算刻度长度
		final int index = (curSlot + (ticks % maxTimers)) % maxTimers; // 放到wheel的位置
		int round = ticks / maxTimers; // wheel旋转的圈数
		if (index == curSlot && round > 0) {
			round -= 1;
		}

		timerSlots[index].add(new TimerTask(round, run));
	}

	@SuppressWarnings("unchecked")
	public void start() {
		if (!start) {
			synchronized (this) {
				if (!start) {
					timerSlots = new ConcurrentLinkedQueue[config.getMaxTimers()];
					for (int i = 0; i < timerSlots.length; i++) {
						timerSlots[i] = new ConcurrentLinkedQueue<TimerTask>();
					}

					start = true;
					new Thread(new Worker(), "firefly time wheel").start();
				}
			}
		}
	}

	public void stop() {
		start = false;
		timerSlots = null;
	}
	
	private final class Worker implements Runnable {

		@Override
		public void run() {
			while(start) {
				int currentSlotTemp = currentSlot;
				ConcurrentLinkedQueue<TimerTask> timerSlot = timerSlots[currentSlotTemp++];
				currentSlotTemp %= timerSlots.length;
				
				for (Iterator<TimerTask> iterator = timerSlot.iterator(); iterator.hasNext();) {
					if (iterator.next().runTask())
						iterator.remove();
				}

				try {
					Thread.sleep(config.getInterval());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				currentSlot = currentSlotTemp;
			}
			
		}
		
	}
	
	private final class TimerTask {
		private int round;
		private Runnable run;

		public TimerTask(int round, Runnable run) {
			this.round = round;
			this.run = run;
		}

		public boolean runTask() {
			if (round == 0) {
				run.run();
				return true;
			} else {
				round--;
				return false;
			}
		}
	}

}
