package com.firefly.utils.json.support;

import java.util.Deque;
import java.util.LinkedList;
import com.firefly.utils.io.StringWriter;

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
}
