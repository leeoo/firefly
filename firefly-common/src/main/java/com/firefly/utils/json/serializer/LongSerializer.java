package com.firefly.utils.json.serializer;

import com.firefly.utils.io.StringWriter;
import com.firefly.utils.json.Serializer;

public class LongSerializer implements Serializer {

	@Override
	public void convertTo(StringWriter writer, Object obj) {
		writer.writeLong((Long)obj);
	}

}
