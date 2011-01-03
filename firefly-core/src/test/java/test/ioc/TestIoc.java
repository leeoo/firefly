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
	public static ApplicationContext applicationContext = DefaultApplicationContext
			.getInstance().load();

	@Test
	public void testFieldInject() {
		FieldInject fieldInject = applicationContext.getBean("fieldInject");
		Assert.assertThat(fieldInject.add(5, 4), is(9));
		Assert.assertThat(fieldInject.add2(5, 4), is(9));

		fieldInject = applicationContext.getBean(FieldInject.class);
		Assert.assertThat(fieldInject.add(5, 4), is(9));
		Assert.assertThat(fieldInject.add2(5, 4), is(9));
	}

	@Test
	public void testMethodInject() {
		MethodInject m = applicationContext.getBean("methodInject");
		Assert.assertThat(m.add(5, 4), is(9));
	}

	@Test
	public void testSingle() {
		AddService t = applicationContext.getBean("addService");
		t.getI();
		t.getI();
		Assert.assertThat(t.getI(), greaterThan(0));
	}
}
