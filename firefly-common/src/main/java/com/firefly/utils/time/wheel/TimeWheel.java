package com.firefly.utils.time.wheel;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class TimeWheel {
	private Config config = new Config();
	private TimerSlot[] timerSlots;
	private int currentSlot = 0;
	private volatile int currentSlotNum = 0;
	private ExecutorService workerThreadPool;
	private volatile boolean start;
	private ScheduledFuture<?> fireHandle;
	private ScheduledExecutorService scheduler;

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
		// System.out.println("maxTimers: " + maxTimers);
		// System.out.println("currentSlotNum: " + currentSlotNum + " index: " +
		// index + " round: " + round);

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
					final boolean hasWorkers = config.getWorkerThreads() > 0;
					if (hasWorkers)
						workerThreadPool = Executors.newFixedThreadPool(config
								.getWorkerThreads());

					scheduler = Executors
							.newSingleThreadScheduledExecutor(new ThreadFactory() {
								@Override
								public Thread newThread(Runnable r) {
									return new Thread(r, "firefly time wheel");
								}
							});

					fireHandle = scheduler.scheduleAtFixedRate(new Runnable() {

						@Override
						public void run() {
							TimerSlot timerSlot = timerSlots[currentSlot++];
							currentSlotNum = timerSlot.getSlotNum();
//							System.out.println("fire: " + timerSlot.getQueue().size());
							for (Iterator<TimerNode> iterator = timerSlot
									.getQueue().iterator(); iterator.hasNext();) {
								TimerNode node = iterator.next();
								if (node.getRound() == 0) {
									if (hasWorkers)
										workerThreadPool.submit(node.getRun());
									else
										node.getRun().run();
									iterator.remove();
								} else {
									node.setRound(node.getRound() - 1);
								}
							}

							if (currentSlot == timerSlots.length)
								currentSlot = 0;
						}
					}, 0, config.getInterval(), TimeUnit.MILLISECONDS);

					start = true;

				}
			}
		}
	}

	public void stop() {
		if (workerThreadPool != null)
			workerThreadPool.shutdown();
		fireHandle.cancel(true);
		scheduler.shutdown();
		start = false;
	}

}
