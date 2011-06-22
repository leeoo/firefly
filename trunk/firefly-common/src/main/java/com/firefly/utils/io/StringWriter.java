package com.firefly.utils.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

public class StringWriter extends Writer {

	protected char buf[];
	protected int count;
	private final static ThreadLocal<char[]> bufLocal = new ThreadLocal<char[]>();

	public StringWriter() {
		buf = bufLocal.get(); // new char[1024];
		if (buf == null) {
			buf = new char[1024];
		} else {
			bufLocal.set(null);
		}
	}

	public StringWriter(int initialSize) {
		if (initialSize < 0) {
			throw new IllegalArgumentException("Negative initial size: "
					+ initialSize);
		}
		buf = new char[initialSize];
	}

	@Override
	public void write(int c) {
		int newcount = count + 1;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		buf[count] = (char) c;
		count = newcount;
	}

	@Override
	public void write(char c[], int off, int len) {
		if (off < 0 || off > c.length || len < 0 || off + len > c.length
				|| off + len < 0) {
			throw new IndexOutOfBoundsException();
		} else if (len == 0) {
			return;
		}

		int newcount = count + len;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		System.arraycopy(c, off, buf, count, len);
		count = newcount;

	}

	@Override
	public void write(String str, int off, int len) {
		int newcount = count + len;
		if (newcount > buf.length) {
			expandCapacity(newcount);
		}
		str.getChars(off, off + len, buf, count);
		count = newcount;
	}

	@Override
	public StringWriter append(CharSequence csq) {
		String s = (csq == null ? "null" : csq.toString());
		write(s, 0, s.length());
		return this;
	}

	@Override
	public StringWriter append(CharSequence csq, int start, int end) {
		String s = (csq == null ? "null" : csq).subSequence(start, end)
				.toString();
		write(s, 0, s.length());
		return this;
	}

	@Override
	public StringWriter append(char c) {
		write(c);
		return this;
	}

	@Override
	public String toString() {
		return new String(buf, 0, count);
	}

	@Override
	public void flush() {
	}

	@Override
	public void close() {
		bufLocal.set(buf);
	}

	public void writeTo(Writer out) throws IOException {
		out.write(buf, 0, count);
	}

	public void writeTo(OutputStream out, String charset) throws IOException {
		byte[] bytes = new String(buf, 0, count).getBytes(charset);
		out.write(bytes);
	}

	public void reset() {
		count = 0;
	}

	public char[] toCharArray() {
		char[] newValue = new char[count];
		System.arraycopy(buf, 0, newValue, 0, count);
		return newValue;
	}

	public int size() {
		return count;
	}

	private void expandCapacity(int minimumCapacity) {
		int newCapacity = (buf.length + 1) * 2;
		if (newCapacity < minimumCapacity) {
			newCapacity = minimumCapacity;
		}
		char newValue[] = new char[newCapacity];
		System.arraycopy(buf, 0, newValue, 0, count);
		buf = newValue;
	}


}
