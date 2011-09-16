package com.firefly.utils.json.serializer;

import java.io.IOException;

import com.firefly.utils.json.Serializer;
import com.firefly.utils.json.support.JsonStringWriter;

public class StringArrayNoFilterSerializer implements Serializer {

	@Override
	public void convertTo(JsonStringWriter writer, Object obj)
			throws IOException {
//		System.out.println("string array no filter");
		String[] object = (String[])obj;
		writer.writeStringArrayNoFilter(object);
	}

}
