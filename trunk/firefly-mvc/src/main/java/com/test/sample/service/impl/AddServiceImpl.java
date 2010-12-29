package com.test.sample.service.impl;

import com.firefly.annotation.Component;
import com.test.sample.service.AddService;

@Component
public class AddServiceImpl implements AddService {
	private int i = 0;

	@Override
	public int add(int x, int y) {
		return x + y + i++;
	}

}
