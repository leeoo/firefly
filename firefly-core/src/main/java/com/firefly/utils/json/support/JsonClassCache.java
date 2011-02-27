package com.firefly.utils.json.support;

import java.util.HashMap;
import java.util.Map;

public class JsonClassCache {
	public final Map<Class<?>, FieldHandle[]> map;

	private JsonClassCache() {
//		map = new ConcurrentHashMap<Class<?>, FieldHandle[]>();
		map = new HashMap<Class<?>, FieldHandle[]>();
	}

	private static class JsonObjCacheHolder {
		private static JsonClassCache instance = new JsonClassCache();
	}

	public static JsonClassCache getInstance() {
		return JsonObjCacheHolder.instance;
	}

	public void put(Class<?> clazz, FieldHandle[] FieldHandles) {
		map.put(clazz, FieldHandles);
	}

	public FieldHandle[] get(Class<?> clazz) {
		return map.get(clazz);
	}

	public void remove(Class<?> clazz) {
		map.remove(clazz);
	}
}
