package com.firefly.utils.timer;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeWheel {
	private static Logger log = LoggerFactory.getLogger(TimeWheel.class);
	private Config config = new Config();
	private List<TimerSlot> list = new LinkedList<TimerSlot>();
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
		int ticks = (int) (delay / config.getInterval()); // 计算刻度长度
		int index = currentSlotNum + (ticks % config.getMaxTimers()); // 放到wheel的位置
		int round = ticks / config.getMaxTimers(); // wheel旋转的圈数

		TimerNode node = new TimerNode();
		node.setRound(round);
		node.setRun(run);
		list.get(index).getQueue().add(node);
	}

	/**
	 * TimeWheel开始旋转
	 */
	public void start() {
		for (int i = 0; i < config.getMaxTimers(); i++) {
			TimerSlot timerSlot = new TimerSlot();
			timerSlot.setSlotNum(i);
			list.add(timerSlot);
		}
		workerThreadPool = Executors.newFixedThreadPool(config
				.getWorkerThreads());
		scheduler = Executors.newScheduledThreadPool(config.getTimerThreads());

		fireHandle = scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				TimerSlot timerSlot = list.get(currentSlot.getAndIncrement());
				currentSlotNum = timerSlot.getSlotNum();
				log.debug("fire: {}", currentSlotNum);

				Iterator<TimerNode> iterator = timerSlot.getQueue().iterator();
				while (iterator.hasNext()) {
					TimerNode node = iterator.next();
					if (node.getRound() == 0) {
						workerThreadPool.submit(node.getRun());
						iterator.remove();
					} else {
						node.setRound(node.getRound() - 1);
					}
				}

				currentSlot.compareAndSet(list.size(), 0);
			}
		}, config.getInitialDelay(), config.getInterval(),
				TimeUnit.MILLISECONDS);
	}

	public void stop() {
		fireHandle.cancel(true);
	}

}
