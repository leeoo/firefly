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
}
