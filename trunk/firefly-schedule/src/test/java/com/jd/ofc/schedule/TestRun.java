package com.jd.ofc.schedule;

import org.junit.Test;

import com.firefly.schedule.common.Config;
import com.firefly.schedule.core.Schedule;

public class TestRun {
	
	@Test
	public void run(){
		Config config = new Config();
		config.setOpenHeart(true);
		Schedule schedule = new TestSchedule();
		schedule.setConfig(config);
		schedule.start();
		try {
			Thread.sleep(100000000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
