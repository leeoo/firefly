package test.component.impl;

import com.firefly.annotation.Component;
import com.firefly.annotation.Inject;

import test.component.AddService;
import test.component.MethodInject;

@Component("methodInject")
public class MethodInjectImpl implements MethodInject {

	private AddService addService;

	@SuppressWarnings("unused")
	@Inject
	private void init(AddService addService, String test) {
		this.addService = addService;
	}

	@Override
	public int add(int x, int y) {
		return addService.add(x, y);
	}

}
