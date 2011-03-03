package com.firefly.utils.json;

public abstract class Json {
	public static String toJson(Object obj) {
		return new JsonSerializer().toJson(obj).toString();
	}
}
