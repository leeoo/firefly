package test.utils;

import com.firefly.utils.StringUtils;

public class SplitPerformance {

	private static final int TIMES = 50000;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = "fdsf@dsfsdf";
		long start = System.currentTimeMillis();
		String t1 = null;
		for (int i = 0; i < TIMES; i++) {
			t1 = str.split("@")[1];
		}
		long end = System.currentTimeMillis();
		System.out.println("String split [" + (end - start) + "ms]" + t1);

		start = System.currentTimeMillis();
		for (int i = 0; i < TIMES; i++) {
			t1 = StringUtils.split(str, "@")[1];
		}
		end = System.currentTimeMillis();
		System.out.println("String split [" + (end - start) + "ms]" + t1);
	}

}
