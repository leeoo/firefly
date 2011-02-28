package com.firefly.utils.json.support;

public class CharTypes {
	private final static boolean[] specicalFlags_doubleQuotes = new boolean[((int) '\\' + 1)];
	private final static char[] replaceChars = new char[((int) '\\' + 1)];
	static {
		specicalFlags_doubleQuotes['\b'] = true;
		specicalFlags_doubleQuotes['\n'] = true;
		specicalFlags_doubleQuotes['\t'] = true;
		specicalFlags_doubleQuotes['\f'] = true;
		specicalFlags_doubleQuotes['\r'] = true;
		specicalFlags_doubleQuotes['\"'] = true;
		specicalFlags_doubleQuotes['\\'] = true;
		specicalFlags_doubleQuotes['/'] = true;

		replaceChars['\b'] = 'b';
		replaceChars['\n'] = 'n';
		replaceChars['\t'] = 't';
		replaceChars['\f'] = 'f';
		replaceChars['\r'] = 'r';
		replaceChars['\"'] = '"';
		replaceChars['\''] = '\'';
		replaceChars['\\'] = '\\';
		replaceChars['/'] = '/';

	}

	public static boolean isSpecicalFlags(char ch) {
		return ch < specicalFlags_doubleQuotes.length
				&& specicalFlags_doubleQuotes[ch];
	}

	public static char replaceChar(char ch) {
		return replaceChars[(int) ch];
	}

	public static void main(String[] args) {
		// System.out.println(replaceChars.length);
		for (char c : replaceChars) {
			if ((int) c != 0)
				System.out.print((int) c + " ");
		}
	}
}
