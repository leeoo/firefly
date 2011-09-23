package com.firefly.utils.time.wheel;

import java.util.Iterator;

public class TimeWheel {
	private Config config = new Config();
	private TimerSlot[] timerSlots;
	private int currentSlot = 0;
	private volatile int currentSlotNum = 0;
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
		final int ticks = delay > config.getInterval() ? (int) (delay / config
				.getInterval()) : 1; // 计算刻度长度
		int index = currentSlotNum + (ticks % maxTimers); // 放到wheel的位置
		if (index >= maxTimers) {
			index -= maxTimers;
		}

		int round = ticks / maxTimers; // wheel旋转的圈数
		if (index <= currentSlotNum && round > 0) {
			round -= 1;
		}

		TimerNode node = new TimerNode();
		node.setRound(round);
		node.setRun(run);
		timerSlots[index].getQueue().add(node);
	}

	/**
	 * TimeWheel开始旋转
	 */
	public void start() {
		if (!start) {
			synchronized (this) {
				if (!start) {
					timerSlots = new TimerSlot[config.getMaxTimers()];
					for (int i = 0; i < timerSlots.length; i++) {
						TimerSlot timerSlot = new TimerSlot();
						timerSlot.setSlotNum(i);
						timerSlots[i] = timerSlot;
					}

					start = true;
					new Thread(new Worker(), "firefly time wheel").start();
				}
			}
		}
	}

	public void stop() {
		start = false;
	}
	
	private final class Worker implements Runnable {

		@Override
		public void run() {
			while(start) {
				TimerSlot timerSlot = timerSlots[currentSlot++];
				currentSlotNum = timerSlot.getSlotNum();

				for (Iterator<TimerNode> iterator = timerSlot
						.getQueue().iterator(); iterator.hasNext();) {
					TimerNode node = iterator.next();
					if (node.getRound() == 0) {
						node.getRun().run();
						iterator.remove();
					} else {
						node.setRound(node.getRound() - 1);
					}
				}

				if (currentSlot == timerSlots.length)
					currentSlot = 0;
				
				try {
					Thread.sleep(config.getInterval());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}

}
