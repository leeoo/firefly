import java.io.OutputStream;
import com.firefly.template.ObjectNavigator;
import com.firefly.template.Model;
import com.firefly.template.view.AbstractView;

public class _head extends AbstractView {

	private _head(){}

	public static final _head INSTANCE = new _head();

	@Override
	protected void main(Model model, OutputStream out) throws Throwable {
		ObjectNavigator objNav = ObjectNavigator.getInstance();
		out.write(_TEXT_0);
		out.write(objNav.getValue(model ,"title").getBytes("UTF-8"));
		out.write(_TEXT_1);
	}

	private final byte[] _TEXT_0 = new byte[]{60, 109, 101, 116, 97, 32, 99, 104, 97, 114, 115, 101, 116, 61, 34, 85, 84, 70, 45, 56, 34, 62, 60, 116, 105, 116, 108, 101, 62};
	private final byte[] _TEXT_1 = new byte[]{60, 47, 116, 105, 116, 108, 101, 62};
}