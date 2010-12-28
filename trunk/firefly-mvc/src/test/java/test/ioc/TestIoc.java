package test.ioc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import org.junit.Assert;
import org.junit.Test;

import test.component.AddService;
import test.component.FieldInject;
import test.component.MethodInject;

import com.firefly.mvc.web.WebContext;
import com.firefly.mvc.web.WebContextHolder;

public class TestIoc {
	private WebContext webContext;

	public TestIoc() {
		webContext = WebContextHolder.getWebContext();
		webContext.load("firefly_mvc.properties");
	}

	@Test
	public void testFieldInject() {
		FieldInject t = (FieldInject) webContext.getBean("fieldInject");
		Assert.assertThat(t.add(5, 4), is(9));
	}

	@Test
	public void testMethodInject() {
		MethodInject m = (MethodInject) webContext.getBean("methodInject");
		Assert.assertThat(m.add(5, 4), is(9));
	}

	@Test
	public void testSingle() {
		AddService t = (AddService) webContext.getBean("addService");
		t.getI();
		t.getI();
		Assert.assertThat(t.getI(), greaterThan(0));
	}
}
