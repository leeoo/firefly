package com.firefly.utils.json.serializer;

import static com.firefly.utils.json.JsonStringSymbol.OBJ_PRE;
import static com.firefly.utils.json.JsonStringSymbol.OBJ_SUF;
import static com.firefly.utils.json.JsonStringSymbol.SEPARATOR;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.firefly.utils.json.ClassCache;
import com.firefly.utils.json.Serializer;
import com.firefly.utils.json.support.JsonClassCache;
import com.firefly.utils.json.support.JsonObjMetaInfo;
import com.firefly.utils.json.support.JsonStringWriter;

public class ObjectSerializer implements Serializer {
	
	private static final ClassCache classCache = JsonClassCache.getInstance();
	private static final JsonObjMetaInfo[] EMPTY_ARRAY = new JsonObjMetaInfo[0];

	@Override
	public void convertTo(JsonStringWriter writer, Object obj) throws IOException {
		if (obj == null)
			return;

		Class<?> clazz = obj.getClass();
		writer.append(OBJ_PRE);
		JsonObjMetaInfo[] list = classCache.get(clazz);
		if (list == null) {
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
//				System.out.println(clazz.getName() + "|" + propertyName);
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

				JsonObjMetaInfo fieldSerializer = new JsonObjMetaInfo();
				fieldSerializer.setPropertyName(propertyName);
				fieldSerializer.setMethod(method);
				fieldList.add(fieldSerializer);
			}
			list = fieldList.toArray(EMPTY_ARRAY);
			classCache.put(clazz, list);
		}
		
		boolean first = true;
		for(JsonObjMetaInfo metaInfo : list){
			if(!first) writer.append(SEPARATOR);
			StateMachine.appendPair(metaInfo.getPropertyName(), metaInfo.invoke(obj), writer);
			if(first) first = false;
		}
		writer.append(OBJ_SUF);

	}

}
