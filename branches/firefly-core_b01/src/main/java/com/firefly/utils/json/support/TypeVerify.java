package com.firefly.utils.json.support;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

abstract public class TypeVerify {
	private static final Set<Class<?>> numberAndBoolTypeSet = new HashSet<Class<?>>();
	private static final Set<Class<?>> stringTypeSet = new HashSet<Class<?>>();
	private static final Set<Class<?>> dateTypeSet = new HashSet<Class<?>>();

	static {
		numberAndBoolTypeSet.add(int.class);
		numberAndBoolTypeSet.add(long.class);
		numberAndBoolTypeSet.add(short.class);
		numberAndBoolTypeSet.add(double.class);
		numberAndBoolTypeSet.add(float.class);
		numberAndBoolTypeSet.add(byte.class);

		numberAndBoolTypeSet.add(Integer.class);
		numberAndBoolTypeSet.add(Long.class);
		numberAndBoolTypeSet.add(Short.class);
		numberAndBoolTypeSet.add(Double.class);
		numberAndBoolTypeSet.add(Float.class);
		numberAndBoolTypeSet.add(Byte.class);

		numberAndBoolTypeSet.add(AtomicInteger.class);
		numberAndBoolTypeSet.add(AtomicLong.class);
		numberAndBoolTypeSet.add(BigDecimal.class);
		numberAndBoolTypeSet.add(BigInteger.class);

		numberAndBoolTypeSet.add(boolean.class);
		numberAndBoolTypeSet.add(Boolean.class);
		numberAndBoolTypeSet.add(AtomicBoolean.class);

		stringTypeSet.add(char.class);
		stringTypeSet.add(Character.class);
		stringTypeSet.add(String.class);
		stringTypeSet.add(StringBuilder.class);
		stringTypeSet.add(StringBuffer.class);

		dateTypeSet.add(java.util.Date.class);
		dateTypeSet.add(java.sql.Date.class);
		dateTypeSet.add(java.sql.Time.class);
		dateTypeSet.add(java.sql.Timestamp.class);
	}

	public static boolean isNumberOrBool(Class<?> clazz) {
		return numberAndBoolTypeSet.contains(clazz);
	}

	public static boolean isString(Class<?> clazz) {
		return stringTypeSet.contains(clazz);
	}

	public static boolean isDateLike(Class<?> clazz) {
		return dateTypeSet.contains(clazz);
	}
}
