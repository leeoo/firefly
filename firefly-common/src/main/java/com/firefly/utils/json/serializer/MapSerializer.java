package com.firefly.utils.json.serializer;

import static com.firefly.utils.json.JsonStringSymbol.OBJ_PRE;
import static com.firefly.utils.json.JsonStringSymbol.OBJ_SUF;
import static com.firefly.utils.json.JsonStringSymbol.SEPARATOR;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.firefly.utils.json.Serializer;
import com.firefly.utils.json.support.JsonStringWriter;

public class MapSerializer implements Serializer {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void convertTo(JsonStringWriter writer, Object obj) throws IOException {
		if (obj == null)
			return;
		
		Map map = (Map)obj;
		writer.append(OBJ_PRE);
		Set<Entry<?, ?>> entrySet = map.entrySet();
		for (Iterator<Entry<?, ?>> it = entrySet.iterator(); it.hasNext();) {
			Entry<?, ?> entry = it.next();
			Object key = entry.getKey();
			if(key == null) {
				writer.writeNull();
			} else {
				writer.write("\"" + key + "\":");
			}
			
			Object val = entry.getValue();
			StateMachine.toJson(val, writer);
			if (it.hasNext())
				writer.append(SEPARATOR);
		}
		writer.append(OBJ_SUF);

	}

}
