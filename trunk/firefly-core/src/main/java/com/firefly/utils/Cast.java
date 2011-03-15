package com.firefly.utils;

abstract public class Cast {

	@SuppressWarnings("unchecked")
	public static <T> T convert(String value, Class<T> c) {
		Object ret = null;
		if (c.equals(Integer.class)) {
			ret = Integer.parseInt(value);
		} else if (c.equals(Long.class)) {
			ret = Long.parseLong(value);
		} else if (c.equals(Double.class)) {
			ret = Double.parseDouble(value);
		} else if (c.equals(Float.class)) {
			ret = Float.parseFloat(value);
		} else if (c.equals(Boolean.class)) {
			ret = Boolean.parseBoolean(value);
		} else if (c.equals(Short.class)) {
			ret = Short.parseShort(value);
		} else if (c.equals(Byte.class)) {
			ret = Byte.parseByte(value);
		} else if (c.equals(String.class)) {
			ret = value;
		}
		return (T) ret;
	}

	@SuppressWarnings("unchecked")
	public static <T> T convert(String value, String argsType) {
		Object ret = null;
		if ("byte".equals(argsType))
			ret = Byte.parseByte(value);
		else if ("short".equals(argsType))
			ret = Short.parseShort(value);
		else if ("int".equals(argsType))
			ret = Integer.parseInt(value);
		else if ("long".equals(argsType))
			ret = Long.parseLong(value);
		else if ("float".equals(argsType))
			ret = Float.parseFloat(value);
		else if ("double".equals(argsType))
			ret = Double.parseDouble(value);
		else if ("boolean".equals(argsType))
			ret = Boolean.parseBoolean(value);
		else if ("java.lang.Byte".equals(argsType))
			ret = new Byte(value);
		else if ("java.lang.Short".equals(argsType))
			ret = new Short(value);
		else if ("java.lang.Integer".equals(argsType))
			ret = new Integer(value);
		else if ("java.lang.Long".equals(argsType))
			ret = new Long(value);
		else if ("java.lang.Float".equals(argsType))
			ret = new Float(value);
		else if ("java.lang.Double".equals(argsType))
			ret = new Double(value);
		else if ("java.lang.Boolean".equals(argsType))
			ret = new Boolean(value);
		else
			ret = value;
		return (T) ret;
	}
}
