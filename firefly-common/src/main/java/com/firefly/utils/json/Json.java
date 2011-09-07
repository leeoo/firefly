package com.firefly.utils.json;

import java.io.IOException;

import com.firefly.utils.io.StringWriter;
import com.firefly.utils.json.serializer.StateMachine;


public abstract class Json {
	public static String toJson(Object obj) {
		String ret = null;
		StringWriter stringWriter = new StringWriter();
		try {
			ret = new StateMachine(stringWriter).toJson(obj).toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			stringWriter.close();
		}
		return ret;
	}

//	public static void toJson(Object obj, Writer writer) {
//		try {
//			new JsonSerializer(writer).toJson(obj);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
