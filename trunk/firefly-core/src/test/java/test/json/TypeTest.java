package test.json;

import com.firefly.utils.json.support.TypeVerify;

public class TypeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Integer j = 1;
		Class<?> clazz = j.getClass();
		boolean b = false;
		long start = System.currentTimeMillis();
		for(int i = 0; i < 10000000; i++) {
			b = TypeVerify.isNumberOrBool(clazz);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println(b);
	}

}
