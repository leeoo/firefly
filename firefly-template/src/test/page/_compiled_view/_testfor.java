import java.io.OutputStream;
import com.firefly.template.support.ObjectNavigator;
import com.firefly.template.Model;
import com.firefly.template.view.AbstractView;

public class _testfor extends AbstractView {

	private _testfor(){}

	public static final _testfor INSTANCE = new _testfor();

	@Override
	protected void main(Model model, OutputStream out) throws Throwable {
		ObjectNavigator objNav = ObjectNavigator.getInstance();
		out.write(_TEXT_0);
		for(Object i : objNav.getCollection(model, "intArr")){
			model.put("i", i);
			out.write(objNav.getValue(model ,"i").getBytes("UTF-8"));
			out.write(_TEXT_1);
		}
		out.write(_TEXT_2);
		for(Object u : objNav.getCollection(model, "users")){
			model.put("u", u);
			out.write(_TEXT_3);
			out.write(objNav.getValue(model ,"u.name").getBytes("UTF-8"));
			out.write(_TEXT_4);
			out.write(objNav.getValue(model ,"u.age").getBytes("UTF-8"));
			out.write(_TEXT_5);
		}
		out.write(_TEXT_6);
	}

	private final byte[] _TEXT_0 = new byte[]{60, 33, 68, 79, 67, 84, 89, 80, 69, 32, 104, 116, 109, 108, 62, 60, 104, 116, 109, 108, 62, 60, 98, 111, 100, 121, 62, 60, 100, 105, 118, 62};
	private final byte[] _TEXT_1 = new byte[]{32, 38, 110, 98, 115, 112, 59, 38, 110, 98, 115, 112, 59};
	private final byte[] _TEXT_2 = new byte[]{60, 47, 100, 105, 118, 62, 60, 100, 105, 118, 62, 60, 116, 97, 98, 108, 101, 32, 115, 116, 121, 108, 101, 61, 34, 116, 97, 98, 108, 101, 45, 108, 97, 121, 111, 117, 116, 58, 32, 102, 105, 120, 101, 100, 59, 34, 62, 60, 116, 104, 101, 97, 100, 62, 60, 116, 114, 62, 60, 116, 104, 62, -27, -89, -109, -27, -112, -115, 60, 47, 116, 104, 62, 60, 116, 104, 62, -27, -71, -76, -23, -66, -124, 60, 47, 116, 104, 62, 60, 47, 116, 114, 62, 60, 47, 116, 104, 101, 97, 100, 62, 60, 116, 98, 111, 100, 121, 62};
	private final byte[] _TEXT_3 = new byte[]{60, 116, 114, 62, 60, 116, 104, 62};
	private final byte[] _TEXT_4 = new byte[]{60, 47, 116, 104, 62, 60, 116, 104, 62};
	private final byte[] _TEXT_5 = new byte[]{60, 47, 116, 104, 62, 60, 47, 116, 114, 62};
	private final byte[] _TEXT_6 = new byte[]{60, 47, 116, 98, 111, 100, 121, 62, 60, 47, 116, 97, 98, 108, 101, 62, 60, 47, 100, 105, 118, 62, 60, 47, 98, 111, 100, 121, 62, 60, 47, 104, 116, 109, 108, 62};
}