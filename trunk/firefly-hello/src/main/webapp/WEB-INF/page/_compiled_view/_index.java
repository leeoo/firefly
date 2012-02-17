import java.io.OutputStream;
import com.firefly.template.support.ObjectNavigator;
import com.firefly.template.Model;
import com.firefly.template.view.AbstractView;
import com.firefly.template.TemplateFactory;
import com.firefly.template.FunctionRegistry;

public class _index extends AbstractView {

	public _index(TemplateFactory templateFactory){this.templateFactory = templateFactory;}

	@Override
	protected void main(Model model, OutputStream out) throws Throwable {
		ObjectNavigator objNav = ObjectNavigator.getInstance();
		out.write(_TEXT_0);
		out.write(objNav.getValue(model ,"hello").getBytes("UTF-8"));
		out.write(_TEXT_1);
		templateFactory.getView("/index_1.html").render(model, out);
		templateFactory.getView("/index_2.html").render(model, out);
		templateFactory.getView("/index_3.html").render(model, out);
		templateFactory.getView("/index_3.html").render(model, out);
		templateFactory.getView("/index_3.html").render(model, out);
		templateFactory.getView("/index_3.html").render(model, out);
		templateFactory.getView("/index_3.html").render(model, out);
		templateFactory.getView("/index_3.html").render(model, out);
		templateFactory.getView("/index_3.html").render(model, out);
		templateFactory.getView("/index_3.html").render(model, out);
		templateFactory.getView("/index_3.html").render(model, out);
		templateFactory.getView("/index_3.html").render(model, out);
		templateFactory.getView("/index_3.html").render(model, out);
		templateFactory.getView("/index_3.html").render(model, out);
		out.write(_TEXT_2);
	}

	private final byte[] _TEXT_0 = new byte[]{60, 33, 68, 79, 67, 84, 89, 80, 69, 32, 104, 116, 109, 108, 62, 60, 104, 116, 109, 108, 62, 60, 104, 101, 97, 100, 62, 60, 116, 105, 116, 108, 101, 62};
	private final byte[] _TEXT_1 = new byte[]{32, -26, -75, -117, -24, -81, -107, -28, -72, -128, -28, -72, -117, -23, -95, -75, -23, -99, -94, 60, 47, 116, 105, 116, 108, 101, 62, 60, 115, 116, 121, 108, 101, 32, 116, 121, 112, 101, 61, 34, 116, 101, 120, 116, 47, 99, 115, 115, 34, 62, 46, 116, 105, 116, 108, 101, 123, 111, 118, 101, 114, 102, 108, 111, 119, 58, 32, 104, 105, 100, 100, 101, 110, 59, 116, 101, 120, 116, 45, 97, 108, 105, 103, 110, 58, 32, 99, 101, 110, 116, 101, 114, 59, 125, 46, 99, 111, 110, 116, 101, 110, 116, 32, 123, 119, 105, 100, 116, 104, 58, 32, 53, 48, 101, 109, 59, 111, 118, 101, 114, 102, 108, 111, 119, 58, 32, 104, 105, 100, 100, 101, 110, 59, 109, 97, 114, 103, 105, 110, 58, 32, 48, 32, 97, 117, 116, 111, 59, 125, 60, 47, 115, 116, 121, 108, 101, 62, 60, 47, 104, 101, 97, 100, 62, 60, 98, 111, 100, 121, 62};
	private final byte[] _TEXT_2 = new byte[]{60, 47, 98, 111, 100, 121, 62, 60, 47, 104, 116, 109, 108, 62};
}