package com.firefly.utils.json;

import static com.firefly.utils.json.JsonStringSymbol.ARRAY_PRE;
import static com.firefly.utils.json.JsonStringSymbol.ARRAY_SUF;
import static com.firefly.utils.json.JsonStringSymbol.NULL;
import static com.firefly.utils.json.JsonStringSymbol.OBJ_PRE;
import static com.firefly.utils.json.JsonStringSymbol.OBJ_SEPARATOR;
import static com.firefly.utils.json.JsonStringSymbol.OBJ_SUF;
import static com.firefly.utils.json.JsonStringSymbol.QUOTE;
import static com.firefly.utils.json.JsonStringSymbol.SEPARATOR;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.firefly.utils.json.support.JsonClassCache;
import com.firefly.utils.json.support.JsonObjMetaInfo;
import com.firefly.utils.json.support.TypeVerify;
import com.firefly.utils.time.SafeSimpleDateFormat;

class JsonSerializer {
	private Writer writer;
	private Set<Object> existence; // 防止循环引用
	private ClassCache classCache;
	private static final JsonObjMetaInfo[] JOM = new JsonObjMetaInfo[0];

	public JsonSerializer(Writer writer) {
		this.writer = writer;
		existence = new HashSet<Object>();
		classCache = JsonClassCache.getInstance();
	}

	JsonSerializer toJson(Object obj) throws IOException {
		if (obj == null) {
			writer.append(NULL);
			return this;
		}
		Class<?> clazz = obj.getClass();
		if (TypeVerify.isNumberOrBool(clazz)) { // 数字，布尔类型
			writer.append(obj.toString());
		} else if (clazz.isEnum()) { // 枚举类型
			string2Json(((Enum<?>) obj).name());
		} else if (TypeVerify.isString(clazz)) { // 字符串或字符类型
			string2Json(obj.toString());
		} else if (TypeVerify.isDateLike(clazz)) {
			string2Json(SafeSimpleDateFormat.defaultDateFormat.format((Date) obj));
		} else if (existence.contains(obj)) { // 防止循环引用，此处会影响一些性能
			writer.append(NULL);
		} else {
			existence.add(obj);
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
		JsonObjMetaInfo[] jsonObjMetaInfo = classCache.get(clazz);
		if (jsonObjMetaInfo == null) {
			List<JsonObjMetaInfo> fieldList = new ArrayList<JsonObjMetaInfo>();
			Method[] methods = clazz.getMethods();
			for (int i = 0; i < methods.length; i++) {
				Method method = methods[i];
				method.setAccessible(true);
				String methodName = method.getName();

				if (Modifier.isStatic(method.getModifiers())
						|| method.getReturnType().equals(Void.TYPE)
						|| method.getParameterTypes().length != 0) {
					continue;
				}

				if (methodName.startsWith("get")) { // 取get方法的返回值
					if (methodName.length() < 4
							|| methodName.equals("getClass")
							|| !Character.isUpperCase(methodName.charAt(3))) {
						continue;
					}

					String propertyName = Character.toLowerCase(methodName
							.charAt(3))
							+ methodName.substring(4);

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

					try {
						JsonObjMetaInfo fieldSerializer = new JsonObjMetaInfo();
						fieldSerializer.setPropertyName(propertyName);
						fieldSerializer.setMethod(method);
						fieldList.add(fieldSerializer);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				} else if (methodName.startsWith("is")) { // 取is方法的返回值
					if (methodName.length() < 3
							|| !Character.isUpperCase(methodName.charAt(2))) {
						continue;
					}

					String propertyName = Character.toLowerCase(methodName
							.charAt(2))
							+ methodName.substring(3);

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

					try {
						JsonObjMetaInfo fieldSerializer = new JsonObjMetaInfo();
						fieldSerializer.setPropertyName(propertyName);
						fieldSerializer.setMethod(method);
						fieldList.add(fieldSerializer);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}

			classCache.put(clazz, fieldList.toArray(JOM));
		}

		if ((jsonObjMetaInfo = classCache.get(clazz)) != null) {
			for (int i = 0; i < jsonObjMetaInfo.length; i++) {
				try {
					appendPair(jsonObjMetaInfo[i].getPropertyName(),
							jsonObjMetaInfo[i].getMethod().invoke(obj));
					if (i < jsonObjMetaInfo.length - 1)
						writer.append(SEPARATOR);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		writer.append(OBJ_SUF);
	}

	@SuppressWarnings("unchecked")
	private void map2Json(Map map) throws IOException {
		if (map == null)
			return;
		writer.append(OBJ_PRE);
		Set<Entry<?, ?>> entrySet = map.entrySet();
		for (Iterator<Entry<?, ?>> it = entrySet.iterator(); it.hasNext();) {
			Entry<?, ?> entry = it.next();
			String name = entry.getKey() == null ? NULL : entry.getKey()
					.toString();
			Object val = entry.getValue();
			appendPair(name, val);
			if (it.hasNext())
				writer.append(SEPARATOR);
		}
		writer.append(OBJ_SUF);
	}

	private void string2Json(String s) throws IOException {
		if (s == null)
			writer.append(NULL);
		else {
			char[] cs = s.toCharArray();
			writer.append(QUOTE);
			for (char ch : cs) {
				switch (ch) {
				case '"':
					writer.append("\\\"");
					break;
				case '\b':
					writer.append("\\b");
					break;
				case '\n':
					writer.append("\\n");
					break;
				case '\t':
					writer.append("\\t");
					break;
				case '\f':
					writer.append("\\f");
					break;
				case '\r':
					writer.append("\\r");
					break;
				case '\\':
					writer.append("\\\\");
					break;
				default:
					writer.append(ch);
				}
			}
			writer.append(QUOTE);
		}
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

	private void appendPair(String name, Object val) throws IOException {
		string2Json(name);
		writer.append(OBJ_SEPARATOR);
		toJson(val);
	}

	@Override
	public String toString() {
		return writer.toString();
	}
}
