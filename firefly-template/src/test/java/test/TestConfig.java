package test;

import static org.hamcrest.Matchers.is;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.firefly.template.Config;
import com.firefly.template.Model;
import com.firefly.template.TemplateFactory;
import com.firefly.template.View;

public class TestConfig {
//	public static final String PATH = "/Users/qiupengtao/Documents/workspace/firefly-project/firefly-template/src/test/page";
	public static final String PATH = "F:/develop/workspace2/firefly-template/src/test/page";
	
	@Test
	public void test() {
		Config config = new Config();
		config.setViewPath("/page");
		Assert.assertThat(config.getCompiledPath(), is("/page/_compiled_view"));
		
		config.setViewPath("/page2/");
		Assert.assertThat(config.getCompiledPath(), is("/page2/_compiled_view"));
	}
	
	public static void main(String[] args) throws IOException {
		User user = new User();
		user.setName("Jim");
		user.setAge(25);

		TemplateFactory t = new TemplateFactory(PATH).init();
		View view = t.getView("/testIf.html");
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Model model = new ModelMock();
		view.render(model, out);
		out.close();
		System.out.println(out.toString());
		
		out = new ByteArrayOutputStream();
		model.put("user", user);
		model.put("login", true);
		view.render(model, out);
		out.close();
		System.out.println(out.toString());
		
		
		model = new ModelMock();
		out = new ByteArrayOutputStream();
		view = t.getView("/testFor.html");
		
		List<User> list = new ArrayList<User>();
		user = new User();
		user.setName("Tom");
		user.setAge(20);
		list.add(user);
		
		user = new User();
		user.setName("小明");
		user.setAge(13);
		list.add(user);
		
		user = new User();
		user.setName("小红");
		user.setAge(20);
		list.add(user);
		
		model.put("users", list);
		model.put("intArr", new int[]{1,2,3,4,5});
		view.render(model, out);
		out.close();
		System.out.println(out.toString());
		
		model = new ModelMock();
		out = new ByteArrayOutputStream();
		view = t.getView("/testSwitch.html");
		model.put("stage", 2);
		view.render(model, out);
		out.close();
		System.out.println(out.toString());
	}
}
