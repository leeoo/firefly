package com.firefly.utils.json.compiler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.firefly.utils.json.Serializer;
import com.firefly.utils.json.annotation.SpecialCharacterFilter;
import com.firefly.utils.json.serializer.StateMachine;
import com.firefly.utils.json.serializer.StringArrayNoFilterSerializer;
import com.firefly.utils.json.serializer.StringArraySerializer;
import com.firefly.utils.json.serializer.StringNoFilterSerializer;
import com.firefly.utils.json.serializer.StringSerializer;
import com.firefly.utils.json.support.JsonObjMetaInfo;

public class EncodeCompiler {
	
	private static final JsonObjMetaInfo[] EMPTY_ARRAY = new JsonObjMetaInfo[0];
	private static final Serializer STRING_NO_FILTER = new StringNoFilterSerializer();
	private static final Serializer STRING_ARRAY_NO_FILTER = new StringArrayNoFilterSerializer();
	
	public static JsonObjMetaInfo[] compile(Class<?> clazz) {
		JsonObjMetaInfo[] jsonObjMetaInfos = null;
		List<JsonObjMetaInfo> fieldList = new ArrayList<JsonObjMetaInfo>();
		
		boolean first = true;
		for (Method method : clazz.getMethods()) {
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
				if (methodName.length() < 4 || !Character.isUpperCase(methodName.charAt(3))) {
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
			fieldJsonObjMetaInfo.setPropertyName(propertyName, first);
			fieldJsonObjMetaInfo.setMethod(method);
			
			Serializer serializer = StateMachine.getSerializer(fieldClazz);
			if(!method.isAnnotationPresent(SpecialCharacterFilter.class)) {
				if(serializer instanceof StringSerializer)
					serializer = STRING_NO_FILTER;
				
				if(serializer instanceof StringArraySerializer)
					serializer = STRING_ARRAY_NO_FILTER;
			}

			fieldJsonObjMetaInfo.setSerializer(serializer);
			fieldList.add(fieldJsonObjMetaInfo);
			first = false;
		}
		
		jsonObjMetaInfos = fieldList.toArray(EMPTY_ARRAY);
		return jsonObjMetaInfos;
	}

}
