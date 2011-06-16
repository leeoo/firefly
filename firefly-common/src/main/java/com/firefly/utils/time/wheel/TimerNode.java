package com.firefly.utils.time.wheel;

public class TimerNode {
	private int round;
	private Runnable run;

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	public Runnable getRun() {
		return run;
	}

	public void setRun(Runnable run) {
		this.run = run;
	}

}
