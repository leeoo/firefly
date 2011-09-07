package com.firefly.utils.json.serializer;

import com.firefly.utils.io.StringWriter;
import com.firefly.utils.json.Serializer;
import static com.firefly.utils.json.JsonStringSymbol.QUOTE;

public class CharacterSerializer implements Serializer {

	@Override
	public void convertTo(StringWriter writer, Object obj) {
		writer.append(QUOTE).append((Character)obj).append(QUOTE);
	}

}
