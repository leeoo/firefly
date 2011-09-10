package com.firefly.utils.json.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.firefly.utils.json.ClassCache;

public class JsonClassCache implements ClassCache {
	private final Map<Class<?>, JsonObjMetaInfo[]> map;

	private JsonClassCache() {
		map = new ConcurrentHashMap<Class<?>, JsonObjMetaInfo[]>();
	}

	private static class Holder {
		private static ClassCache instance = new JsonClassCache();
	}

	public static ClassCache getInstance() {
		return Holder.instance;
	}

	public void put(Class<?> clazz, JsonObjMetaInfo[] obj) {
		map.put(clazz, obj);
	}

	public JsonObjMetaInfo[] get(Class<?> clazz) {
		return map.get(clazz);
	}
}
