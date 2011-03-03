package com.firefly.utils.json;

import com.firefly.utils.json.support.FieldHandle;

public interface ClassCache {

	public abstract void put(Class<?> clazz, FieldHandle[] FieldHandles);

	public abstract FieldHandle[] get(Class<?> clazz);

}