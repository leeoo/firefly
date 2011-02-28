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

	private static class JsonObjCacheHolder {
		private static ClassCache instance = new JsonClassCache();
	}

	public static ClassCache getInstance() {
		return JsonObjCacheHolder.instance;
	}

	/* (non-Javadoc)
	 * @see com.firefly.utils.json.support.ClassCache#put(java.lang.Class, com.firefly.utils.json.support.FieldHandle[])
	 */
	public void put(Class<?> clazz, FieldHandle[] FieldHandles) {
		map.put(clazz, FieldHandles);
	}

	/* (non-Javadoc)
	 * @see com.firefly.utils.json.support.ClassCache#get(java.lang.Class)
	 */
	public FieldHandle[] get(Class<?> clazz) {
		return map.get(clazz);
	}
}
