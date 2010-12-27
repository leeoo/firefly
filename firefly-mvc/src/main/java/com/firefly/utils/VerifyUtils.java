package com.firefly.utils;

abstract public class VerifyUtils {

	public static boolean isNumeric(String str) {
		if (isEmpty(str)) {
			return false;
		}
		int sz = str.length();
		for (int i = 0; i < sz; i++) {
			if (isDigit(str.charAt(i)) == false) {
				return false;
			}
		}
		return true;
	}

	public static boolean isDigit(char ch) {
		return ch >= '0' && ch <= '9';
	}

	public static boolean isNotEmpty(Long o) {
		return o != null && o.toString().length() > 0;
	}

	public static boolean isNotEmpty(Integer o) {
		return o != null && o.toString().length() > 0;
	}

	public static boolean isNotEmpty(String o) {
		return o != null && o.toString().length() > 0;
	}

	public static boolean isEmpty(Long o) {
		return o == null || o.toString().length() == 0;
	}

	public static boolean isEmpty(Integer o) {
		return o == null || o.toString().length() == 0;
	}

	public static boolean isEmpty(String o) {
		return o == null || o.toString().length() == 0;
	}

	public static void main(String[] args) {
		System.out.println(isNumeric("13422224343"));
		System.out.println(isNumeric(""));
		System.out.println(isNumeric("134"));
		System.out.println(isNumeric("134dfdfsfdf"));
	}
}
