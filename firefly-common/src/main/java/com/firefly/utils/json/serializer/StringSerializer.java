package com.firefly.utils.json.serializer;

import static com.firefly.utils.json.JsonStringSymbol.QUOTE;

import com.firefly.utils.io.StringWriter;
import com.firefly.utils.json.Serializer;

public class StringSerializer implements Serializer {

	@Override
	public void convertTo(StringWriter writer, Object obj) {
		String s = obj.toString();
		if (s == null)
			writer.writeNull();
		else {
			char[] cs = s.toCharArray();
			writer.append(QUOTE);
			for (char ch : cs) {
				switch (ch) {
				case '"':
					writer.append("\\\"");
					break;
				case '\b':
					writer.append("\\b");
					break;
				case '\n':
					writer.append("\\n");
					break;
				case '\t':
					writer.append("\\t");
					break;
				case '\f':
					writer.append("\\f");
					break;
				case '\r':
					writer.append("\\r");
					break;
				case '\\':
					writer.append("\\\\");
					break;
				default:
					writer.append(ch);
				}
			}
			writer.append(QUOTE);
		}

	}

}
