package com.firefly.utils.json.serializer;

import static com.firefly.utils.json.JsonStringSymbol.ARRAY_PRE;
import static com.firefly.utils.json.JsonStringSymbol.ARRAY_SUF;
import static com.firefly.utils.json.JsonStringSymbol.OBJ_PRE;
import static com.firefly.utils.json.JsonStringSymbol.OBJ_SEPARATOR;
import static com.firefly.utils.json.JsonStringSymbol.OBJ_SUF;
import static com.firefly.utils.json.JsonStringSymbol.QUOTE;
import static com.firefly.utils.json.JsonStringSymbol.SEPARATOR;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.firefly.utils.StringUtils;
import com.firefly.utils.io.StringWriter;
import com.firefly.utils.json.ClassCache;
import com.firefly.utils.json.Serializer;
import com.firefly.utils.json.support.JsonClassCache;
import com.firefly.utils.json.support.JsonObjMetaInfo;

public class StateMachine {
	private StringWriter writer;
	private IdentityHashMap<Object, Object> existence; // 防止循环引用
	private static final IdentityHashMap<Class<?>, Serializer> map = new IdentityHashMap<Class<?>, Serializer>();
	private static final ClassCache classCache = JsonClassCache.getInstance();
	private static final JsonObjMetaInfo[] EMPTY_ARRAY = new JsonObjMetaInfo[0];
	
	static {
		map.put(long.class, new LongSerializer());
		map.put(int.class, new IntSerializer());
		map.put(char.class, new CharacterSerializer());
		map.put(short.class, new ShortSerializer());
		map.put(byte.class, new ByteSerializer());
		map.put(boolean.class, new BoolSerializer());
		map.put(String.class, new StringSerializer());
		map.put(Date.class, new DateSerializer());
		map.put(double.class, new StringValueSerializer());

		map.put(Long.class, map.get(long.class));
		map.put(Integer.class, map.get(int.class));
		map.put(Character.class, map.get(char.class));
		map.put(Short.class, map.get(short.class));
		map.put(Byte.class, map.get(byte.class));
		map.put(Boolean.class, map.get(boolean.class));

		map.put(StringBuilder.class, map.get(String.class));
		map.put(StringBuffer.class, map.get(String.class));

		map.put(java.sql.Date.class, map.get(Date.class));
		map.put(java.sql.Time.class, map.get(Date.class));
		map.put(java.sql.Timestamp.class, map.get(Date.class));

		map.put(Double.class, map.get(double.class));
		map.put(float.class, map.get(double.class));
		map.put(Float.class, map.get(double.class));
		map.put(AtomicInteger.class, map.get(double.class));
		map.put(AtomicLong.class, map.get(double.class));
		map.put(BigDecimal.class, map.get(double.class));
		map.put(BigInteger.class, map.get(double.class));
		map.put(AtomicBoolean.class, map.get(double.class));
		map.put(Enum.class, map.get(double.class));

	}

	public StateMachine(StringWriter writer) {
		this.writer = writer;
		existence = new IdentityHashMap<Object, Object>();
	}

	public static Serializer getSerializer(Class<?> clazz) {
		return map.get(clazz);
	}

	public StateMachine toJson(Object obj) throws IOException {
		if (obj == null) {
			writer.writeNull();
			return this;
		}
		Class<?> clazz = obj.getClass();
		Serializer serializer = getSerializer(clazz);
		if (serializer != null) {
			serializer.convertTo(writer, obj);
		} else {
			if (existence.get(obj) != null) { // 防止循环引用，此处会影响一些性能
				writer.writeNull();
				return this;
			}
			existence.put(obj, StringUtils.EMPTY);
			if (obj instanceof Map<?, ?>) {
				map2Json((Map<?, ?>) obj);
			} else if (obj instanceof Collection<?>) {
				collection2Json((Collection<?>) obj);
			} else if (clazz.isArray()) {
				array2Json(obj);
			} else { // pojo类型
				pojo2Json(obj);
			}
			existence.remove(obj);
		}

		return this;
	}
	
	private void pojo2Json(Object obj) throws IOException {
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
			appendPair(metaInfo.getPropertyName(), metaInfo.invoke(obj));
			if(first) first = false;
		}
		writer.append(OBJ_SUF);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void map2Json(Map map) throws IOException {
		if (map == null)
			return;
		writer.append(OBJ_PRE);
		Set<Entry<?, ?>> entrySet = map.entrySet();
		for (Iterator<Entry<?, ?>> it = entrySet.iterator(); it.hasNext();) {
			Entry<?, ?> entry = it.next();
			char[] name = entry.getKey() == null ? StringWriter.NULL : entry
					.getKey().toString().toCharArray();
			Object val = entry.getValue();
			appendPair(name, val);
			if (it.hasNext())
				writer.append(SEPARATOR);
		}
		writer.append(OBJ_SUF);
	}

	private void collection2Json(Collection<?> obj) throws IOException {
		writer.append(ARRAY_PRE);
		for (Iterator<?> it = obj.iterator(); it.hasNext();) {
			toJson(it.next());
			if (it.hasNext())
				writer.append(SEPARATOR);
		}
		writer.append(ARRAY_SUF);
	}

	private void array2Json(Object obj) throws IOException {
		writer.append(ARRAY_PRE);
		int len = Array.getLength(obj) - 1;
		if (len > -1) {
			int i;
			for (i = 0; i < len; i++) {
				toJson(Array.get(obj, i));
				writer.append(SEPARATOR);
			}
			toJson(Array.get(obj, i));
		}
		writer.append(ARRAY_SUF);
	}

	void appendPair(char[] name, Object val) throws IOException {
		writer.append(QUOTE).write(name);
		writer.append(QUOTE).append(OBJ_SEPARATOR);
		toJson(val);
	}

	@Override
	public String toString() {
		return writer.toString();
	}
}
