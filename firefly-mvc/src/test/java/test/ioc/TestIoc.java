package test.ioc;

import static org.hamcrest.Matchers.*;
import org.junit.Assert;
import org.junit.Test;
import test.component.AddService;
import test.component.FieldInject;
import test.component.MethodInject;
import com.firefly.core.ApplicationContext;
import com.firefly.core.DefaultApplicationContext;

public class TestIoc {
	private ApplicationContext applicationContext;

	public TestIoc() {
		applicationContext = DefaultApplicationContext.getInstance().load();
	}

	@Test
	public void testFieldInject() {
		FieldInject t = (FieldInject) applicationContext.getBean("fieldInject");
		Assert.assertThat(t.add(5, 4), is(9));
		Assert.assertThat(t.add2(5, 4), is(9));
	}

	@Test
	public void testMethodInject() {
		MethodInject m = (MethodInject) applicationContext
				.getBean("methodInject");
		Assert.assertThat(m.add(5, 4), is(9));
	}

	@Test
	public void testSingle() {
		AddService t = (AddService) applicationContext.getBean("addService");
		t.getI();
		t.getI();
		Assert.assertThat(t.getI(), greaterThan(0));
	}
}
