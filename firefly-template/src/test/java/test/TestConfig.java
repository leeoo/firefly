package test;

import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.firefly.template.Config;
import com.firefly.template.Model;
import com.firefly.template.TemplateFactory;
import com.firefly.template.View;

public class TestConfig {
	public static final String PATH = "/Users/qiupengtao/Documents/workspace/firefly-project/firefly-template/src/test/page";

	@Test
	public void test() {
		Config config = new Config();
		config.setViewPath("/page");
		Assert.assertThat(config.getCompiledPath(), is("/page/_compiled_view"));
		
		config.setViewPath("/page2/");
		Assert.assertThat(config.getCompiledPath(), is("/page2/_compiled_view"));
	}
	
	public static void main(String[] args) {
		User user = new User();
		user.setName("Jim");
		user.setAge(25);
		
//		String path = "F:/develop/workspace2/firefly-template/src/test/page";
		TemplateFactory t = new TemplateFactory(PATH).init();
		View view = t.getView("/testif.html");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Model model = new ModelMock();
		view.render(model, out);
		System.out.println(out.toString());
		
		out = new ByteArrayOutputStream();
		model.put("user", user);
		model.put("login", true);
		view.render(model, out);
		System.out.println(out.toString());
	}
}
