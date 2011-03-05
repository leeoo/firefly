package test.component2.impl;

import test.component.AddService;
import test.component.FieldInject;
import test.component2.MethodInject2;

import com.firefly.annotation.Component;
import com.firefly.annotation.Inject;

@Component("methodInject2")
public class MethodInjectImpl2 implements MethodInject2 {

	@Inject
	private Integer num = 3;
	@Inject
	private AddService addService;
	protected FieldInject fieldInject;

	@Inject
	public void init(AddService addService, FieldInject fieldInject, String str) {
		this.addService = addService;
		this.fieldInject = fieldInject;
		fieldInject.add(3, 4);
	}

	@Override
	public int add(int x, int y) {
		return addService.add(x, y);
	}

	public Integer getNum() {
		return num;
	}

}
