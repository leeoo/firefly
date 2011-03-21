package com.firefly.utils;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.LinkedBlockingDeque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract public class ConvertUtils {
	private static Logger log = LoggerFactory.getLogger(ConvertUtils.class);

	@SuppressWarnings("unchecked")
	public static <T> T convert(String value, Class<T> c) {
		Object ret = null;
		if (c.equals(int.class) || c.equals(Integer.class))
			ret = Integer.parseInt(value);
		else if (c.equals(long.class) || c.equals(Long.class))
			ret = Long.parseLong(value);
		else if (c.equals(double.class) || c.equals(Double.class))
			ret = Double.parseDouble(value);
		else if (c.equals(float.class) || c.equals(Float.class))
			ret = Float.parseFloat(value);
		else if (c.equals(boolean.class) || c.equals(Boolean.class))
			ret = Boolean.parseBoolean(value);
		else if (c.equals(short.class) || c.equals(Short.class))
			ret = Short.parseShort(value);
		else if (c.equals(byte.class) || c.equals(Byte.class))
			ret = Byte.parseByte(value);
		else if (c.equals(String.class))
			ret = value;
		else {
			if (VerifyUtils.isNumeric(value)) {
				long v = Long.parseLong(value);
				if (v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE)
					ret = Integer.parseInt(value);
				else
					ret = v;
			} else if (VerifyUtils.isDouble(value)) {
				ret = Double.parseDouble(value);
			} else
				ret = value;
		}
		return (T) ret;
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(String value, String argsType) {
		Object ret = null;
		if ("byte".equals(argsType) || "java.lang.Byte".equals(argsType))
			ret = Byte.parseByte(value);
		else if ("short".equals(argsType) || "java.lang.Short".equals(argsType))
			ret = Short.parseShort(value);
		else if ("int".equals(argsType) || "java.lang.Integer".equals(argsType))
			ret = Integer.parseInt(value);
		else if ("long".equals(argsType) || "java.lang.Long".equals(argsType))
			ret = Long.parseLong(value);
		else if ("float".equals(argsType) || "java.lang.Float".equals(argsType))
			ret = Float.parseFloat(value);
		else if ("double".equals(argsType)
				|| "java.lang.Double".equals(argsType))
			ret = Double.parseDouble(value);
		else if ("boolean".equals(argsType)
				|| "java.lang.Boolean".equals(argsType))
			ret = Boolean.parseBoolean(value);
		else if ("java.lang.String".equals(argsType))
			ret = value;
		else {
			if (VerifyUtils.isNumeric(value)) {
				long v = Long.parseLong(value);
				if (v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE)
					ret = Integer.parseInt(value);
				else
					ret = v;
			} else if (VerifyUtils.isDouble(value)) {
				ret = Double.parseDouble(value);
			} else
				ret = value;
		}
		return (T) ret;
	}

	/**
	 * 把集合转换为指定类型的数组
	 *
	 * @param collection
	 * @param type
	 * @return
	 */
	public static Object convert(Collection<?> collection, Class<?> arrayType) {
		int size = collection.size();
		// Allocate a new Array
		Iterator<?> iterator = collection.iterator();
		Class<?> componentType = null;

		if (arrayType == null) {
			componentType = Object.class;
		} else {
			if (!arrayType.isArray())
				throw new IllegalArgumentException("type is not a array");

			componentType = arrayType.getComponentType();
			log.debug("componentType = " + componentType.getName());
		}
		Object newArray = Array.newInstance(componentType, size);

		// Convert and set each element in the new Array
		for (int i = 0; i < size; i++) {
			Object element = iterator.next();
			log.debug("element value [{}], type [{}]", element, element
					.getClass().getName());
			Array.set(newArray, i, element);
		}

		return newArray;
	}

	/**
	 * 根据类型自动返回一个集合
	 *
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Collection getCollectionObj(Class<?> clazz) {
		if (clazz.isInterface()) {
			if (clazz.isAssignableFrom(List.class))
				return new ArrayList();
			else if (clazz.isAssignableFrom(Set.class))
				return new HashSet();
			else if (clazz.isAssignableFrom(Queue.class))
				return new ArrayDeque();
			else if (clazz.isAssignableFrom(SortedSet.class))
				return new TreeSet();
			else if (clazz.isAssignableFrom(BlockingQueue.class))
				return new LinkedBlockingDeque();
			else
				return null;
		} else {
			Collection collection = null;
			try {
				collection = (Collection) clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return collection;
		}
	}

	@SuppressWarnings("unchecked")
	public static Map getMapObj(Class<?> clazz) {
		if (clazz.isInterface()) {
			if (clazz.isAssignableFrom(Map.class))
				return new HashMap();
			else if (clazz.isAssignableFrom(ConcurrentMap.class))
				return new ConcurrentHashMap();
			else if (clazz.isAssignableFrom(SortedMap.class))
				return new TreeMap();
			else if (clazz.isAssignableFrom(NavigableMap.class))
				return new TreeMap();
			else if (clazz.isAssignableFrom(ConcurrentNavigableMap.class))
				return new ConcurrentSkipListMap();
			else
				return null;
		} else {
			Map map = null;
			try {
				map = (Map) clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return map;
		}
	}
}
