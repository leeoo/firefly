package com.test.sample.service.impl;

import com.firefly.annotation.Component;
import com.test.sample.service.Test2Service;

@Component
public class Test2ServiceImpl implements Test2Service {

	@Override
	public int add(int x, int y) {
		return x + y;
	}

}
