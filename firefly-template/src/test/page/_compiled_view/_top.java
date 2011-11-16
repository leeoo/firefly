import java.io.OutputStream;
import com.firefly.template.support.ObjectNavigator;
import com.firefly.template.Model;
import com.firefly.template.view.AbstractView;

public class _top extends AbstractView {

	private _top(){}

	public static final _top INSTANCE = new _top();

	@Override
	protected void main(Model model, OutputStream out) throws Throwable {
		ObjectNavigator objNav = ObjectNavigator.getInstance();
		out.write(_TEXT_0);
		out.write(objNav.getValue(model ,"title").getBytes("UTF-8"));
		out.write(_TEXT_1);
	}

	private final byte[] _TEXT_0 = new byte[]{60, 100, 105, 118, 62};
	private final byte[] _TEXT_1 = new byte[]{32, 38, 110, 98, 115, 112, 59, 32, 38, 103, 116, 59, 32, 38, 110, 98, 115, 112, 59, 32, -23, -90, -106, -23, -95, -75, 60, 47, 100, 105, 118, 62};
}