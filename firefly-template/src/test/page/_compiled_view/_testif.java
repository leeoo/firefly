import java.io.OutputStream;
import com.firefly.template.ObjectNavigator;
import com.firefly.template.Model;
import com.firefly.template.view.AbstractView;

public class _testif extends AbstractView {

	private _testif(){}

	public static final _testif INSTANCE = new _testif();

	@Override
	protected void main(Model model, OutputStream out) throws Throwable {
		ObjectNavigator objNav = ObjectNavigator.getInstance();
		out.write(_TEXT_0);
		out.write(_TEXT_1);
		out.write(_TEXT_2);
		if (objNav.getBoolean(model ,"login")){
			out.write(_TEXT_3);
			out.write(objNav.getValue(model ,"user.name").getBytes("UTF-8"));
			out.write(_TEXT_4);
		} else {
			out.write(_TEXT_5);
		}
		out.write(_TEXT_6);
		if (objNav.getLong(model ,"user.age") > 18){
			out.write(_TEXT_7);
		}
		if (objNav.getLong(model ,"user.age") >= 30){
			out.write(_TEXT_8);
		}
		out.write(_TEXT_9);
		if (objNav.getValue(model ,"user.name").equals("Pengtao Qiu")){
			out.write(_TEXT_10);
		} else if (objNav.getValue(model ,"user.name").equals("Bob")){
			out.write(_TEXT_11);
		} else if (objNav.getValue(model ,"user.name").equals("Jim")){
			out.write(_TEXT_12);
		} else {
			out.write(_TEXT_13);
		}
		out.write(_TEXT_14);
	}

	private final byte[] _TEXT_0 = new byte[]{60, 33, 68, 79, 67, 84, 89, 80, 69, 32, 104, 116, 109, 108, 62, 60, 104, 116, 109, 108, 62, 60, 104, 101, 97, 100, 62};
	private final byte[] _TEXT_1 = new byte[]{60, 47, 104, 101, 97, 100, 62, 60, 98, 111, 100, 121, 62};
	private final byte[] _TEXT_2 = new byte[]{60, 100, 105, 118, 62};
	private final byte[] _TEXT_3 = new byte[]{87, 101, 108, 99, 111, 109, 101, 32};
	private final byte[] _TEXT_4 = new byte[]{};
	private final byte[] _TEXT_5 = new byte[]{-26, -126, -88, -26, -105, -96, -26, -77, -107, -24, -82, -65, -23, -105, -82};
	private final byte[] _TEXT_6 = new byte[]{60, 47, 100, 105, 118, 62};
	private final byte[] _TEXT_7 = new byte[]{60, 100, 105, 118, 62, -27, -71, -76, -23, -66, -124, -27, -92, -89, -28, -70, -114, 49, 56, 60, 47, 100, 105, 118, 62};
	private final byte[] _TEXT_8 = new byte[]{60, 100, 105, 118, 62, -27, -71, -76, -23, -66, -124, -28, -72, -115, -27, -80, -113, -28, -70, -114, 51, 48, 60, 47, 100, 105, 118, 62};
	private final byte[] _TEXT_9 = new byte[]{60, 100, 105, 118, 62};
	private final byte[] _TEXT_10 = new byte[]{-27, -97, -114, -28, -72, -69, -26, -99, -91, -28, -70, -122};
	private final byte[] _TEXT_11 = new byte[]{-27, -114, -88, -27, -72, -120, -26, -99, -91, -28, -70, -122};
	private final byte[] _TEXT_12 = new byte[]{74, 105, 109, -26, -99, -91, -28, -70, -122};
	private final byte[] _TEXT_13 = new byte[]{-27, -80, -113, -25, -67, -105, -25, -67, -105, -26, -99, -91, -28, -70, -122};
	private final byte[] _TEXT_14 = new byte[]{60, 47, 100, 105, 118, 62, 60, 47, 98, 111, 100, 121, 62, 60, 47, 104, 116, 109, 108, 62};
}