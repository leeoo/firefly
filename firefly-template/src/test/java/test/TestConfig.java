package test;

import static org.hamcrest.Matchers.is;

import org.junit.Assert;
import org.junit.Test;

import com.firefly.template.Config;
import com.firefly.template.parser.ViewFileReader;

public class TestConfig {

	@Test
	public void test() {
		Config config = new Config();
		config.setViewPath("/page");
		Assert.assertThat(config.getCompiledPath(), is("/page/_compiled_view"));
		
		config.setViewPath("/page2/");
		Assert.assertThat(config.getCompiledPath(), is("/page2/_compiled_view"));
	}
	
	public static void main(String[] args) {
//		String path = "F:/develop/workspace2/firefly-template/src/test/page";
		String path = "/Users/qiupengtao/Documents/workspace/firefly-project/firefly-template/src/test/page";
		Config config = new Config();
		config.setViewPath(path);
		ViewFileReader reader = new ViewFileReader();
		reader.setConfig(config);
		reader.readAndBuild();
	}
}
