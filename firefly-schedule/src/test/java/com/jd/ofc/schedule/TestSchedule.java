package com.jd.ofc.schedule;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.firefly.schedule.core.support.TaskDefinition;
import com.firefly.schedule.core.support.schedule.AbstractSchedule;


public class TestSchedule extends AbstractSchedule {

	@Override
	protected List<TaskDefinition> register() throws SQLException {
		List<TaskDefinition> list = new ArrayList<TaskDefinition>();
		
		return list;
	}

	

}
