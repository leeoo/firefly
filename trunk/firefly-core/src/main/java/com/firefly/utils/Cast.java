package com.firefly.utils;

abstract public class Cast {

	@SuppressWarnings("unchecked")
	public static <T> T convert(String value, Class<T> c) {
		Object ret = null;
		if (c.equals(int.class) || c.equals(Integer.class)) {
			ret = Integer.parseInt(value);
		} else if (c.equals(long.class) || c.equals(Long.class)) {
			ret = Long.parseLong(value);
		} else if (c.equals(double.class) || c.equals(Double.class)) {
			ret = Double.parseDouble(value);
		} else if (c.equals(float.class) || c.equals(Float.class)) {
			ret = Float.parseFloat(value);
		} else if (c.equals(boolean.class) || c.equals(Boolean.class)) {
			ret = Boolean.parseBoolean(value);
		} else if (c.equals(short.class) || c.equals(Short.class)) {
			ret = Short.parseShort(value);
		} else if (c.equals(byte.class) || c.equals(Byte.class)) {
			ret = Byte.parseByte(value);
		} else {
			if (VerifyUtils.isNumeric(value))
				ret = Integer.parseInt(value);
			else
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
		else {
			if (VerifyUtils.isNumeric(value))
				ret = Integer.parseInt(value);
			else
				ret = value;
		}
		return (T) ret;
	}
}
