package com.firefly.utils.json;

import com.firefly.utils.json.support.JsonObjMetaInfo;

public interface ClassCache {

	public abstract void put(Class<?> clazz, JsonObjMetaInfo[] jsonObjMetaInfo);

	public abstract JsonObjMetaInfo[] get(Class<?> clazz);

}