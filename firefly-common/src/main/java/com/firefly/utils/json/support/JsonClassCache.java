package com.firefly.utils.json.support;

import java.util.HashMap;
import java.util.Map;

import com.firefly.utils.json.ClassCache;

public class JsonClassCache implements ClassCache {
	public final Map<Class<?>, JsonObjMetaInfo[]> map;

	private JsonClassCache() {
//		map = new ConcurrentHashMap<Class<?>, JsonObjMetaInfo[]>();
		map = new HashMap<Class<?>, JsonObjMetaInfo[]>();
	}

	private static class Holder {
		private static ClassCache instance = new JsonClassCache();
	}

	public static ClassCache getInstance() {
		return Holder.instance;
	}

	public void put(Class<?> clazz, JsonObjMetaInfo[] jsonObjMetaInfo) {
		map.put(clazz, jsonObjMetaInfo);
	}

	public JsonObjMetaInfo[] get(Class<?> clazz) {
		return map.get(clazz);
	}
}
