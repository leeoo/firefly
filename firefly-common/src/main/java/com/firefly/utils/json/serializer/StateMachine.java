package com.firefly.utils.json.serializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.firefly.utils.json.Serializer;
import com.firefly.utils.json.support.JsonStringWriter;

abstract public class StateMachine {
	private static final IdentityHashMap<Class<?>, Serializer> map = new IdentityHashMap<Class<?>, Serializer>();
	private static final Serializer OBJECT = new ObjectSerializer();
	private static final Serializer MAP = new MapSerializer();
	private static final Serializer COLLECTION = new CollectionSerializer();
	private static final Serializer ARRAY = new ArraySerializer();
	private static final Serializer ENUM = new EnumSerializer();

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
	}

	public static Serializer getSimpleSerializer(Class<?> clazz) {
		return map.get(clazz);
	}

	public static Serializer getObjectSerializer(Object obj, Class<?> clazz) {
		if (obj instanceof Map<?, ?>) {
			return MAP;
		} else if (obj instanceof Collection<?>) {
			return COLLECTION;
		} else if (clazz.isArray()) {
			return ARRAY;
		} else if (clazz.isEnum()){
			return ENUM;
		} else {
			return OBJECT;
		}
	}

	public static void toJson(Object obj, JsonStringWriter writer)
			throws IOException {
		if (obj == null) {
			writer.writeNull();
			return;
		}

		Class<?> clazz = obj.getClass();
		Serializer serializer = getSimpleSerializer(clazz);
		if (serializer != null) {
			serializer.convertTo(writer, obj);
		} else {
			if (writer.existRef(obj)) { // 防止循环引用，此处会影响一些性能
				writer.writeNull();
				return;
			}
			writer.pushRef(obj);
			getObjectSerializer(obj, clazz).convertTo(writer, obj);
			writer.popRef();
		}
	}

	static void appendPair(char[] name, Object val, JsonStringWriter writer)
			throws IOException {
		writer.write(name);
		toJson(val, writer);
	}
}
