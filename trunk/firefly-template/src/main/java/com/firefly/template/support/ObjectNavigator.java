package com.firefly.template.support;

import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import com.firefly.template.Config;
import com.firefly.template.Model;
import com.firefly.template.exception.ExpressionError;
import com.firefly.utils.ReflectUtils;
import com.firefly.utils.StringUtils;

public class ObjectNavigator {
	private ObjectMetaInfoCache cache;
	private IdentityHashMap<Class<?>, ArrayObj> map;

	private ObjectNavigator() {
		this.cache = new ObjectMetaInfoCache();
		this.map = new IdentityHashMap<Class<?>, ArrayObj>();

		map.put(long[].class, new ArrayObj() {
			@Override
			public Object get(Object obj, int index) {
				return ((long[])obj)[index];
			}
		});
		map.put(double[].class, new ArrayObj() {
			@Override
			public Object get(Object obj, int index) {
				return ((double[])obj)[index];
			}
		});
		map.put(int[].class, new ArrayObj() {
			@Override
			public Object get(Object obj, int index) {
				return ((int[])obj)[index];
			}
		});
		map.put(float[].class, new ArrayObj() {
			@Override
			public Object get(Object obj, int index) {
				return ((float[])obj)[index];
			}
		});
		map.put(char[].class, new ArrayObj() {
			@Override
			public Object get(Object obj, int index) {
				return ((char[])obj)[index];
			}
		});
		map.put(boolean[].class, new ArrayObj() {
			@Override
			public Object get(Object obj, int index) {
				return ((boolean[])obj)[index];
			}
		});
		map.put(short[].class, new ArrayObj() {
			@Override
			public Object get(Object obj, int index) {
				return ((short[])obj)[index];
			}
		});
		map.put(byte[].class, new ArrayObj() {
			@Override
			public Object get(Object obj, int index) {
				return ((byte[])obj)[index];
			}
		});
	}

	interface ArrayObj {
		Object get(Object obj, int index);
	}

	private static class Holder {
		private static ObjectNavigator instance = new ObjectNavigator();
	}

	public static ObjectNavigator getInstance() {
		return Holder.instance;
	}
	
	public Integer getInteger(Model model, String el) {
		Object ret = find(model, el);
		return ret != null ? ((Number)ret).intValue() : 0;
	}
	
	public Float getFloat(Model model, String el) {
		Object ret = find(model, el);
		return ret != null ? ((Number)ret).floatValue() : 0F;
	}
	
	public Long getLong(Model model, String el) {
		Object ret = find(model, el);
		return ret != null ? ((Number)ret).longValue() : 0L;
	}
	
	public Double getDouble(Model model, String el) {
		Object ret = find(model, el);
		return ret != null ? ((Number)ret).doubleValue() : 0.0;
	}
	
	public Boolean getBoolean(Model model, String el) {
		Object ret = find(model, el);
		return ret != null ? (Boolean)ret : false;
	}

	public String getValue(Model model, String el) {
		Object ret = find(model, el);
		return ret != null ? String.valueOf(ret) : "";
	}

	public Object find(Model model, String el) {
		Object current = null;
		String[] elements = StringUtils.split(el, '.');
		if ((elements != null) && (elements.length > 0)) {
			current = getObject(model, elements[0]);
			if (current == null)
				return null;

			for (int i = 1; i < elements.length; i++) {
				current = getObject(current, elements[i]);
			}
		}
		return current;
	}

	private Object getObject(Object current, String el) {
		boolean root = current instanceof Model;
		String element = el.trim();
		int listOrMapPrefixIndex = element.indexOf('[');
		if (listOrMapPrefixIndex > 0) { // map or list or array
			int listOrMapSuffixIndex = element.indexOf(']',
					listOrMapPrefixIndex);
			if (listOrMapSuffixIndex != element.length() - 1)
				throw new ExpressionError("list or map expression error: "
						+ element);

			String keyEl = element.substring(listOrMapPrefixIndex + 1,
					listOrMapSuffixIndex);
			String p = element.substring(0, listOrMapPrefixIndex);
			Object obj = root ? ((Model) current).get(p) : getObjectProperty(
					current, p);

			if (isMapKey(keyEl)) { // map
				if ((obj instanceof Map))
					return ((Map<?, ?>) obj).get(keyEl.substring(1,
							keyEl.length() - 1));
			} else { // list or array
				int index = Integer.parseInt(keyEl);
				if ((obj instanceof List))
					return ((List<?>) obj).get(index);
				Class<?> c = obj.getClass();
				ArrayObj a = map.get(c);
				if(a != null)
					return a.get(obj, index);
				if (c.isArray()) {
					return ((Object[])obj)[index];
				}
			}
		} else if (listOrMapPrefixIndex < 0) { // object
			return root ? ((Model) current).get(element) : getObjectProperty(
					current, element);
		} else {
			throw new ExpressionError("expression error: " + element);
		}
		return null;
	}

	private boolean isMapKey(String el) {
		char head = el.charAt(0);
		char tail = el.charAt(el.length() - 1);
		return ((head == '\'') && (tail == '\''))
				|| ((head == '"') && (tail == '"'));
	}

	private Object getObjectProperty(Object current, String propertyName) {
		Class<?> clazz = current.getClass();
		Method method = cache.get(clazz, propertyName);
		if (method == null) {
			Config.LOG.debug("no cache: " + clazz.getName() + "|"
					+ propertyName);
			method = ReflectUtils.getGetterMethod(clazz, propertyName);
			cache.put(clazz, propertyName, method);
		}

		Object ret = null;
		try {
			ret = method.invoke(current);
		} catch (Throwable e) {
			Config.LOG.error("getObjectProperty error", e);
		}
		return ret;
	}

}
