import java.io.OutputStream;
import com.firefly.template.support.ObjectNavigator;
import com.firefly.template.Model;
import com.firefly.template.view.AbstractView;
import com.firefly.template.TemplateFactory;
import com.firefly.template.FunctionRegistry;

public class _users extends AbstractView {

	public _users(TemplateFactory templateFactory){this.templateFactory = templateFactory;}

	@Override
	protected void main(Model model, OutputStream out) throws Throwable {
		ObjectNavigator objNav = ObjectNavigator.getInstance();
		out.write(_TEXT_0);
		int user_index = -1;
		for(Object user : objNav.getCollection(model, "users")){
			user_index++;
			model.put("user", user);
			model.put("user_index", user_index);
			out.write(_TEXT_1);
			out.write(objNav.getValue(model ,"user.id").getBytes("UTF-8"));
			out.write(_TEXT_2);
			out.write(objNav.getValue(model ,"user.name").getBytes("UTF-8"));
			out.write(_TEXT_3);
			out.write(objNav.getValue(model ,"user.password").getBytes("UTF-8"));
			out.write(_TEXT_4);
		}
		out.write(_TEXT_5);
	}

	private final byte[] _TEXT_0 = new byte[]{60, 33, 68, 79, 67, 84, 89, 80, 69, 32, 104, 116, 109, 108, 62, 60, 104, 116, 109, 108, 62, 60, 104, 101, 97, 100, 62, 60, 109, 101, 116, 97, 32, 99, 104, 97, 114, 115, 101, 116, 61, 34, 85, 84, 70, 45, 56, 34, 62, 60, 116, 105, 116, 108, 101, 62, 102, 105, 114, 101, 102, 108, 121, 45, 117, 115, 101, 114, 115, 60, 47, 116, 105, 116, 108, 101, 62, 60, 47, 104, 101, 97, 100, 62, 60, 98, 111, 100, 121, 62, 60, 116, 97, 98, 108, 101, 62};
	private final byte[] _TEXT_1 = new byte[]{60, 116, 114, 62, 60, 116, 100, 62};
	private final byte[] _TEXT_2 = new byte[]{60, 47, 116, 100, 62, 60, 116, 100, 62};
	private final byte[] _TEXT_3 = new byte[]{60, 47, 116, 100, 62, 60, 116, 100, 62};
	private final byte[] _TEXT_4 = new byte[]{60, 47, 116, 100, 62, 60, 47, 116, 114, 62};
	private final byte[] _TEXT_5 = new byte[]{60, 47, 116, 97, 98, 108, 101, 62, 60, 47, 98, 111, 100, 121, 62, 60, 47, 104, 116, 109, 108, 62};
}