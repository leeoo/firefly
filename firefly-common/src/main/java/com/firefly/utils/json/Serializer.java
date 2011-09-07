package com.firefly.utils.json;

import com.firefly.utils.io.StringWriter;

public interface Serializer {
	void convertTo(StringWriter writer, Object obj);
}
