package com.firefly.utils.json.support;

import java.util.IdentityHashMap;

import com.firefly.utils.StringUtils;
import com.firefly.utils.io.StringWriter;

public class JsonStringWriter extends StringWriter {
	private IdentityHashMap<Object, Object> existence = new IdentityHashMap<Object, Object>(); // 防止循环引用
	
	public void addRef(Object obj) {
		existence.put(obj, StringUtils.EMPTY);
	}
	
	public boolean existRef(Object obj) {
		return existence.containsKey(obj);
	}
	
	public void removeRef(Object obj) {
		existence.remove(obj);
	}
}
