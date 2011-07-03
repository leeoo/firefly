package test;

import org.junit.Assert;
import org.junit.Test;

import com.firefly.template.Config;

import static org.hamcrest.Matchers.*;

public class TestConfig {

	@Test
	public void test() {
		Config config = new Config();
		config.setViewPath("/page");
		Assert.assertThat(config.getCompiledPath(), is("/page/_compiled_view"));
		
		config.setViewPath("/page2/");
		Assert.assertThat(config.getCompiledPath(), is("/page2/_compiled_view"));
	}
}
