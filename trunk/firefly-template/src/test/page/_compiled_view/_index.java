import java.io.OutputStream;
import com.firefly.template.ObjectNavigator;
import com.firefly.template.Model;
import com.firefly.template.view.AbstractView;

public class _index extends AbstractView {

	private _index(){}

	public static final _index INSTANCE = new _index();

	@Override
	protected void main(Model model, OutputStream out) throws Throwable {
		out.write(_TEXT_0);
		out.write(_TEXT_1);
		out.write(_TEXT_2);
		out.write(_TEXT_3);
		out.write(_TEXT_4);
		out.write(_TEXT_5);
		out.write(_TEXT_6);
		out.write(_TEXT_7);
		out.write(_TEXT_8);
	}

	private static final byte[] _TEXT_0 = new byte[]{60, 33, 68, 79, 67, 84, 89, 80, 69, 32, 104, 116, 109, 108, 62, 60, 104, 116, 109, 108, 62, 60, 104, 101, 97, 100, 62};
	private static final byte[] _TEXT_1 = new byte[]{60, 47, 104, 101, 97, 100, 62, 60, 98, 111, 100, 121, 62};
	private static final byte[] _TEXT_2 = new byte[]{60, 100, 105, 118, 62};
	private static final byte[] _TEXT_3 = new byte[]{87, 101, 108, 99, 111, 109, 101, 32, 36, 123, 117, 115, 101, 114, 46, 110, 97, 109, 101, 125};
	private static final byte[] _TEXT_4 = new byte[]{-26, -126, -88, -26, -105, -96, -26, -77, -107, -24, -82, -65, -23, -105, -82};
	private static final byte[] _TEXT_5 = new byte[]{60, 47, 100, 105, 118, 62};
	private static final byte[] _TEXT_6 = new byte[]{60, 100, 105, 118, 62, -27, -97, -114, -28, -72, -69, -26, -99, -91, -28, -70, -122, 60, 47, 100, 105, 118, 62};
	private static final byte[] _TEXT_7 = new byte[]{60, 100, 105, 118, 62, 36, 123, 110, 97, 109, 101, 125, 60, 47, 100, 105, 118, 62};
	private static final byte[] _TEXT_8 = new byte[]{60, 47, 98, 111, 100, 121, 62, 60, 47, 104, 116, 109, 108, 62};
}