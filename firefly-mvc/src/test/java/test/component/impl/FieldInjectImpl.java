package test.component.impl;

import test.component.FieldInject;
import test.component.AddService;
import com.firefly.annotation.Component;
import com.firefly.annotation.Inject;

@Component("fieldInject")
public class FieldInjectImpl implements FieldInject {

	@Inject
	private AddService addService;

	@Override
	public int add(int x, int y) {
		return addService.add(x, y);
	}

}
