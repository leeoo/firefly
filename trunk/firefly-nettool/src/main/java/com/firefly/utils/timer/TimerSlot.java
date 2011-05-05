package com.firefly.utils.timer;

import java.util.concurrent.ConcurrentLinkedQueue;

public class TimerSlot {
	private ConcurrentLinkedQueue<TimerNode> queue = new ConcurrentLinkedQueue<TimerNode>();
	private int slotNum;

	public int getSlotNum() {
		return slotNum;
	}

	public void setSlotNum(int slotNum) {
		this.slotNum = slotNum;
	}

	public ConcurrentLinkedQueue<TimerNode> getQueue() {
		return queue;
	}	

}
