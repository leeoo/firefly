package com.firefly.utils.json.support;

import java.util.Deque;
import java.util.LinkedList;

import com.firefly.utils.io.StringWriter;
import static com.firefly.utils.json.JsonStringSymbol.QUOTE;

public class JsonStringWriter extends StringWriter {
	private Deque<Object> deque = new LinkedList<Object>();

	public void pushRef(Object obj) {
		deque.addFirst(obj);
	}

	public boolean existRef(Object obj) {
		return deque.contains(obj);
	}

	public void popRef() {
		deque.removeFirst();
	}

	public void write(final boolean quote, final String value) {
		int len = value.length();
		int newcount = count + len + (quote ? 2 : 0);
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		if (quote)
			buf[count++] = QUOTE;
		value.getChars(0, len, buf, count);
		if (quote)
			buf[count++] = QUOTE;
		count += len;
	}

	public void writeJsonString(final String value) {
		int newcount = count + (value.length() * 2);
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		buf[count++] = QUOTE;
		for (char ch : value.toCharArray()) {
			switch (ch) {
			case '"':
				buf[count++] = '\\';
				buf[count++] = '"';
				break;
			case '\b':
				buf[count++] = '\\';
				buf[count++] = 'b';
				break;
			case '\n':
				buf[count++] = '\\';
				buf[count++] = 'n';
				break;
			case '\t':
				buf[count++] = '\\';
				buf[count++] = 't';
				break;
			case '\f':
				buf[count++] = '\\';
				buf[count++] = 'f';
				break;
			case '\r':
				buf[count++] = '\\';
				buf[count++] = 'r';
				break;
			case '\\':
				buf[count++] = '\\';
				buf[count++] = '\\';
				break;
			default:
				buf[count++] = ch;
			}
		}
		buf[count++] = QUOTE;
	}
}
