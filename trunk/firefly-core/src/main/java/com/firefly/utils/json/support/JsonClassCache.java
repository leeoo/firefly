package com.firefly.utils.json.support;

import java.util.HashMap;
import java.util.Map;

import com.firefly.utils.json.ClassCache;

public class JsonClassCache implements ClassCache {
	public final Map<Class<?>, FieldHandle[]> map;

	private JsonClassCache() {
//		map = new ConcurrentHashMap<Class<?>, FieldHandle[]>();
		map = new HashMap<Class<?>, FieldHandle[]>();
	}

	private static class Holder {
		private static ClassCache instance = new JsonClassCache();
	}

	public static ClassCache getInstance() {
		return Holder.instance;
	}

	public void put(Class<?> clazz, FieldHandle[] FieldHandles) {
		map.put(clazz, FieldHandles);
	}

	public FieldHandle[] get(Class<?> clazz) {
		return map.get(clazz);
	}
}
