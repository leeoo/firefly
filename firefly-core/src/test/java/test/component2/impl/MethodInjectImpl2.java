package test.component2.impl;

import com.firefly.annotation.Component;
import com.firefly.annotation.Inject;

import test.component.AddService;
import test.component2.MethodInject2;

@Component("methodInject2")
public class MethodInjectImpl2 implements MethodInject2 {

	@Inject
	private AddService addService;

	@Inject
	public void init(AddService addService) {
		this.addService = addService;
	}

	@Override
	public int add(int x, int y) {
		return addService.add(x, y);
	}

}
