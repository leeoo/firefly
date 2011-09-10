package com.firefly.utils.json.compiler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.firefly.utils.json.ClassCache;
import com.firefly.utils.json.serializer.StateMachine;
import com.firefly.utils.json.support.JsonClassCache;
import com.firefly.utils.json.support.JsonObjMetaInfo;

public class EncodeCompiler {
	
	private static final ClassCache classCache = JsonClassCache.getInstance();
	private static final JsonObjMetaInfo[] EMPTY_ARRAY = new JsonObjMetaInfo[0];
	
	public static JsonObjMetaInfo[] compile(Object obj, Class<?> clazz) {
		JsonObjMetaInfo[] jsonObjMetaInfos = null;
		List<JsonObjMetaInfo> fieldList = new ArrayList<JsonObjMetaInfo>();
		
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			method.setAccessible(true);
			String methodName = method.getName();
			
			if (method.getName().length() < 3) continue;
            if (Modifier.isStatic(method.getModifiers())) continue;
            if (Modifier.isAbstract(method.getModifiers())) continue;
            if (method.getName().equals("getClass")) continue;
            if (!method.getName().startsWith("is") && !method.getName().startsWith("get")) continue;
            if (method.getParameterTypes().length != 0) continue;
            if (method.getReturnType() == void.class) continue;

            String propertyName = null;
			if (methodName.charAt(0) == 'g') {
				if (methodName.length() < 4
						|| !Character.isUpperCase(methodName.charAt(3))) {
					continue;
				}

				propertyName = Character.toLowerCase(methodName
						.charAt(3)) + methodName.substring(4);
			} else {
				if (methodName.length() < 3
						|| !Character.isUpperCase(methodName.charAt(2))) {
					continue;
				}

				propertyName = Character.toLowerCase(methodName
						.charAt(2)) + methodName.substring(3);
			}
			
			Field field = null;
//			System.out.println(clazz.getName() + "|" + propertyName);
			try {
				field = clazz.getDeclaredField(propertyName);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}

			if (field != null
					&& Modifier.isTransient(field.getModifiers())) {
				continue;
			}

			Class<?> fieldClazz = method.getReturnType();
			JsonObjMetaInfo fieldJsonObjMetaInfo = new JsonObjMetaInfo();
			fieldJsonObjMetaInfo.setPropertyName(propertyName);
			fieldJsonObjMetaInfo.setMethod(method);
			fieldJsonObjMetaInfo.setSerializer(StateMachine.getSerializer(fieldClazz));

			fieldList.add(fieldJsonObjMetaInfo);
		}
		
		jsonObjMetaInfos = fieldList.toArray(EMPTY_ARRAY);
		classCache.put(clazz, jsonObjMetaInfos);
		return jsonObjMetaInfos;
	}

}
