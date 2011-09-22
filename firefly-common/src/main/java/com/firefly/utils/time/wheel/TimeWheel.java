package com.firefly.utils.time.wheel;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeWheel {
	private Config config = new Config();
	private TimerSlot[] timerSlots;
	private AtomicInteger currentSlot = new AtomicInteger(0);
	private volatile int currentSlotNum = 0;
	private ScheduledFuture<?> fireHandle;
	private ExecutorService workerThreadPool;
	private ScheduledExecutorService scheduler;

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}

	public ScheduledFuture<?> getFireHandle() {
		return fireHandle;
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
		final int ticks = delay > config.getInterval() ? (int) (delay / config.getInterval()) : 1; // 计算刻度长度
		int index = currentSlotNum + (ticks % maxTimers); // 放到wheel的位置
		if(index >= maxTimers) {
			index -= maxTimers;
		}
		
		int round = ticks / maxTimers; // wheel旋转的圈数
		if(index <= currentSlotNum && round > 0) {
			round -= 1;
		}
//		System.out.println("maxTimers: " + maxTimers);
//		System.out.println("currentSlotNum: " + currentSlotNum + " index: " + index + " round: " + round);
		
		TimerNode node = new TimerNode();
		node.setRound(round);
		node.setRun(run);
		timerSlots[index].getQueue().add(node);
	}

	/**
	 * TimeWheel开始旋转
	 */
	public void start() {
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
		scheduler = Executors.newScheduledThreadPool(config.getTimerThreads());

		fireHandle = scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				TimerSlot timerSlot = timerSlots[currentSlot.getAndIncrement()];
				currentSlotNum = timerSlot.getSlotNum();
//				System.out.println("fire: " + currentSlotNum);
				for (Iterator<TimerNode> iterator = timerSlot.getQueue()
						.iterator(); iterator.hasNext();) {
					TimerNode node = iterator.next();
//					System.out.println("round: " + node.getRound());
					if (node.getRound() == 0) {
						if (hasWorkers)
							workerThreadPool.submit(node.getRun());
						else
							node.getRun().run();
						iterator.remove();
					} else {
						node.setRound(node.getRound() - 1);
					}
//					System.out.println("iterator: " + iterator.hasNext());
				}
//				System.out.println("wheel end: " + currentSlotNum);
				currentSlot.compareAndSet(timerSlots.length, 0);
			}
		}, config.getInitialDelay(), config.getInterval(),
				TimeUnit.MILLISECONDS);
	}

	public void stop() {
		if (workerThreadPool != null)
			workerThreadPool.shutdown();
		fireHandle.cancel(true);
		scheduler.shutdown();
	}

}
