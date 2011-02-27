package com.firefly.utils.json;

import static com.firefly.utils.json.JsonStringSymbol.ARRAY_PRE;
import static com.firefly.utils.json.JsonStringSymbol.ARRAY_SUF;
import static com.firefly.utils.json.JsonStringSymbol.NULL;
import static com.firefly.utils.json.JsonStringSymbol.OBJ_PRE;
import static com.firefly.utils.json.JsonStringSymbol.OBJ_SEPARATOR;
import static com.firefly.utils.json.JsonStringSymbol.OBJ_SUF;
import static com.firefly.utils.json.JsonStringSymbol.QUOTE;
import static com.firefly.utils.json.JsonStringSymbol.SEPARATOR;
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
import com.firefly.utils.SafeSimpleDateFormat;
import com.firefly.utils.json.support.FieldHandle;
import com.firefly.utils.json.support.JsonClassCache;
import com.firefly.utils.json.support.TypeVerify;

public class JsonSerializer {
	private StringBuilder sb;
	private Set<Object> existence; // 防止循环引用

	public JsonSerializer() {
		sb = new StringBuilder();
		existence = new HashSet<Object>();
	}

	JsonSerializer toJson(Object obj) {
		if (obj == null) {
			sb.append(NULL);
			return this;
		}
		Class<?> clazz = obj.getClass();
		if (TypeVerify.isNumberOrBool(clazz)) { // 数字，布尔类型
			sb.append(obj);
		} else if (clazz.isEnum()) { // 枚举类型
			string2Json(((Enum<?>) obj).name());
		} else if (TypeVerify.isString(clazz)) { // 字符串或字符类型
			string2Json(obj.toString());
		} else if (TypeVerify.isDateLike(clazz)) {
			string2Json(SafeSimpleDateFormat.safeFormatDate((Date) obj));
		} else if (existence.contains(obj)) { // 防止循环引用，此处会影响一些性能
			sb.append(NULL);
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

	private void pojo2Json(Object obj) {
		if (obj == null)
			return;
		Class<?> clazz = obj.getClass();
		sb.append(OBJ_PRE);
		List<Pair> list = new ArrayList<Pair>();
		FieldHandle[] FieldHandles = JsonClassCache.getInstance().get(clazz);
		if (FieldHandles == null) {
			List<FieldHandle> fieldList = new ArrayList<FieldHandle>();
			for (Method method : clazz.getMethods()) {
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
						FieldHandle fieldSerializer = new FieldHandle();
						fieldSerializer.setPropertyName(propertyName);
						fieldSerializer.setMethod(method);
						fieldList.add(fieldSerializer);
						list.add(new Pair(propertyName, method.invoke(obj)));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}

				if (methodName.startsWith("is")) { // 取is方法的返回值
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
						FieldHandle fieldSerializer = new FieldHandle();
						fieldSerializer.setPropertyName(propertyName);
						fieldSerializer.setMethod(method);
						fieldList.add(fieldSerializer);
						list.add(new Pair(propertyName, method.invoke(obj)));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}

			JsonClassCache.getInstance().put(clazz,
					fieldList.toArray(new FieldHandle[0]));
		} else {
			for (FieldHandle fieldHandle : FieldHandles) {
				try {
					list.add(new Pair(fieldHandle.getPropertyName(),
							fieldHandle.getMethod().invoke(obj)));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		for (Iterator<Pair> it = list.iterator(); it.hasNext();) {
			Pair p = it.next();
			appendPair(p);
			if (it.hasNext())
				sb.append(SEPARATOR);
		}
		sb.append(OBJ_SUF);
	}

	@SuppressWarnings("unchecked")
	private void map2Json(Map map) {
		if (map == null)
			return;
		sb.append(OBJ_PRE);
		ArrayList<Pair> list = new ArrayList<Pair>(map.size());
		Set<Entry<?, ?>> entrySet = map.entrySet();
		for (Entry entry : entrySet) {
			String name = entry.getKey() == null ? NULL : entry.getKey()
					.toString();
			Object value = entry.getValue();
			list.add(new Pair(name, value));
		}
		for (Iterator<Pair> it = list.iterator(); it.hasNext();) {
			Pair p = it.next();
			appendPair(p);
			if (it.hasNext())
				sb.append(SEPARATOR);
		}
		sb.append(OBJ_SUF);
	}

	private void string2Json(String s) {
		if (s == null)
			sb.append(NULL);
		else {
			char[] cs = s.toCharArray();
			sb.append(QUOTE);
			for (char c : cs) {
				switch (c) {
				case '"':
					sb.append("\\\"");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\t':
					sb.append("\\t");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				default:
					sb.append(c);
				}
			}
			sb.append(QUOTE);
		}
	}

	private void collection2Json(Collection<?> obj) {
		sb.append(ARRAY_PRE);
		for (Iterator<?> it = obj.iterator(); it.hasNext();) {
			toJson(it.next());
			if (it.hasNext())
				sb.append(SEPARATOR);
		}
		sb.append(ARRAY_SUF);
	}

	private void array2Json(Object obj) {
		sb.append(ARRAY_PRE);
		int len = Array.getLength(obj) - 1;
		if (len > -1) {
			int i;
			for (i = 0; i < len; i++) {
				toJson(Array.get(obj, i));
				sb.append(SEPARATOR);
			}
			toJson(Array.get(obj, i));
		}
		sb.append(ARRAY_SUF);
	}

	private void appendPair(Pair pair) {
		string2Json(pair.name);
		sb.append(OBJ_SEPARATOR);
		toJson(pair.val);
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}
