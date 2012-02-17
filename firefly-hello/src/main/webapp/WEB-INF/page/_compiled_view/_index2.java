import java.io.OutputStream;
import com.firefly.template.support.ObjectNavigator;
import com.firefly.template.Model;
import com.firefly.template.view.AbstractView;
import com.firefly.template.TemplateFactory;
import com.firefly.template.FunctionRegistry;

public class _index2 extends AbstractView {

	public _index2(TemplateFactory templateFactory){this.templateFactory = templateFactory;}

	@Override
	protected void main(Model model, OutputStream out) throws Throwable {
		ObjectNavigator objNav = ObjectNavigator.getInstance();
		out.write(_TEXT_0);
	}

	private final byte[] _TEXT_0 = new byte[]{-26, -75, -117, -24, -81, -107, -28, -72, -128, -28, -72, -117, -23, -95, -75, -23, -99, -94};
}