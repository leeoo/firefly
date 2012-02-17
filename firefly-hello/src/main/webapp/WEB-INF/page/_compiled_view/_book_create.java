import java.io.OutputStream;
import com.firefly.template.support.ObjectNavigator;
import com.firefly.template.Model;
import com.firefly.template.view.AbstractView;
import com.firefly.template.TemplateFactory;
import com.firefly.template.FunctionRegistry;

public class _book_create extends AbstractView {

	public _book_create(TemplateFactory templateFactory){this.templateFactory = templateFactory;}

	@Override
	protected void main(Model model, OutputStream out) throws Throwable {
		ObjectNavigator objNav = ObjectNavigator.getInstance();
		out.write(_TEXT_0);
		FunctionRegistry.get("url").render(model, out, "/app/book/create");
		out.write(_TEXT_1);
	}

	private final byte[] _TEXT_0 = new byte[]{60, 33, 68, 79, 67, 84, 89, 80, 69, 32, 104, 116, 109, 108, 62, 60, 104, 116, 109, 108, 62, 60, 104, 101, 97, 100, 62, 60, 116, 105, 116, 108, 101, 62, 102, 105, 114, 101, 102, 108, 121, 60, 47, 116, 105, 116, 108, 101, 62, 60, 47, 104, 101, 97, 100, 62, 60, 98, 111, 100, 121, 62, 60, 102, 111, 114, 109, 32, 97, 99, 116, 105, 111, 110, 61, 39};
	private final byte[] _TEXT_1 = new byte[]{39, 32, 109, 101, 116, 104, 111, 100, 61, 34, 80, 79, 83, 84, 34, 62, -28, -71, -90, -27, -112, -115, -17, -68, -102, 60, 105, 110, 112, 117, 116, 32, 116, 121, 112, 101, 61, 34, 116, 101, 120, 116, 34, 32, 110, 97, 109, 101, 61, 34, 116, 105, 116, 108, 101, 34, 62, 60, 47, 105, 110, 112, 117, 116, 62, 60, 98, 114, 47, 62, -28, -69, -73, -26, -96, -68, -17, -68, -102, 60, 105, 110, 112, 117, 116, 32, 116, 121, 112, 101, 61, 34, 116, 101, 120, 116, 34, 32, 110, 97, 109, 101, 61, 34, 112, 114, 105, 99, 101, 34, 62, 60, 47, 105, 110, 112, 117, 116, 62, 60, 98, 114, 47, 62, 60, 105, 110, 112, 117, 116, 32, 116, 121, 112, 101, 61, 34, 115, 117, 98, 109, 105, 116, 34, 32, 118, 97, 108, 117, 101, 61, 34, -26, -113, -112, -28, -70, -92, 34, 62, 60, 47, 105, 110, 112, 117, 116, 62, 60, 47, 102, 111, 114, 109, 62, 60, 47, 98, 111, 100, 121, 62, 60, 47, 104, 116, 109, 108, 62};
}