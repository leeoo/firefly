package com.firefly.utils.json;

import java.io.IOException;
import java.io.Writer;

public abstract class Json {
	public static String toJson(Object obj) {
		String ret = null;
		try {
			ret = new JsonSerializer(new SerializeWriter()).toJson(obj).toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static void toJson(Object obj, Writer writer) {
		try {
			new JsonSerializer(writer).toJson(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
