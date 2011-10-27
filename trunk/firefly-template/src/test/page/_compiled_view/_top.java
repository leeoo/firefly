import java.io.OutputStream;
import com.firefly.template.ObjectNavigator;
import com.firefly.template.Model;
import com.firefly.template.view.AbstractView;

public class _top extends AbstractView {

	private _top(){}

	public static final _top INSTANCE = new _top();

	@Override
	protected void main(Model model, OutputStream out) throws Throwable {
		out.write(_TEXT_0);
	}

	private static final byte[] _TEXT_0 = new byte[]{60, 100, 105, 118, 62, 36, 123, 116, 105, 116, 108, 101, 125, 32, 38, 110, 98, 115, 112, 59, 32, 38, 103, 116, 59, 32, 38, 110, 98, 115, 112, 59, 32, -23, -90, -106, -23, -95, -75, 60, 47, 100, 105, 118, 62};
}