package com.firefly.utils.json.serializer;

import java.util.Date;
import static com.firefly.utils.json.JsonStringSymbol.QUOTE;
import com.firefly.utils.io.StringWriter;
import com.firefly.utils.json.Serializer;
import com.firefly.utils.time.SafeSimpleDateFormat;

public class DateSerializer implements Serializer {

	@Override
	public void convertTo(StringWriter writer, Object obj) {
		writer.write(QUOTE + SafeSimpleDateFormat.defaultDateFormat.format((Date) obj) + QUOTE);
	}

}
