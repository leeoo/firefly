import java.io.OutputStream;
import com.firefly.template.support.ObjectNavigator;
import com.firefly.template.Model;
import com.firefly.template.view.AbstractView;
import com.firefly.template.TemplateFactory;
import com.firefly.template.FunctionRegistry;

public class _book extends AbstractView {

	public _book(TemplateFactory templateFactory){this.templateFactory = templateFactory;}

	@Override
	protected void main(Model model, OutputStream out) throws Throwable {
		ObjectNavigator objNav = ObjectNavigator.getInstance();
		out.write(_TEXT_0);
		out.write(objNav.getValue(model ,"book.id").getBytes("UTF-8"));
		out.write(_TEXT_1);
		out.write(objNav.getValue(model ,"book.title").getBytes("UTF-8"));
		out.write(_TEXT_2);
		out.write(objNav.getValue(model ,"book.text").getBytes("UTF-8"));
		out.write(_TEXT_3);
		out.write(objNav.getValue(model ,"book.price").getBytes("UTF-8"));
		out.write(_TEXT_4);
		out.write(objNav.getValue(model ,"book.sell").getBytes("UTF-8"));
		out.write(_TEXT_5);
	}

	private final byte[] _TEXT_0 = new byte[]{60, 33, 68, 79, 67, 84, 89, 80, 69, 32, 104, 116, 109, 108, 62, 60, 104, 116, 109, 108, 62, 60, 104, 101, 97, 100, 62, 60, 116, 105, 116, 108, 101, 62, 102, 105, 114, 101, 102, 108, 121, 60, 47, 116, 105, 116, 108, 101, 62, 60, 47, 104, 101, 97, 100, 62, 60, 98, 111, 100, 121, 62, 124, 32};
	private final byte[] _TEXT_1 = new byte[]{32, 124, 32};
	private final byte[] _TEXT_2 = new byte[]{32, 124, 32};
	private final byte[] _TEXT_3 = new byte[]{32, 124, 32};
	private final byte[] _TEXT_4 = new byte[]{32, 124, 32};
	private final byte[] _TEXT_5 = new byte[]{32, 124, 60, 47, 98, 111, 100, 121, 62, 60, 47, 104, 116, 109, 108, 62};
}