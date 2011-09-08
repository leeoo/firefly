package com.firefly.utils.json;

import java.io.IOException;

import com.firefly.utils.io.StringWriter;

public interface Serializer {
	void convertTo(StringWriter writer, Object obj) throws IOException;
}
