package com.firefly.utils.json.serializer;

import static com.firefly.utils.json.JsonStringSymbol.OBJ_PRE;
import static com.firefly.utils.json.JsonStringSymbol.OBJ_SUF;
import static com.firefly.utils.json.JsonStringSymbol.SEPARATOR;

import java.io.IOException;

import com.firefly.utils.json.ClassCache;
import com.firefly.utils.json.Serializer;
import com.firefly.utils.json.compiler.EncodeCompiler;
import com.firefly.utils.json.support.JsonClassCache;
import com.firefly.utils.json.support.JsonObjMetaInfo;
import com.firefly.utils.json.support.JsonStringWriter;

public class ObjectSerializer implements Serializer {
	
	private static final ClassCache classCache = JsonClassCache.getInstance();

	@Override
	public void convertTo(JsonStringWriter writer, Object obj) throws IOException {
		if (obj == null) {
			writer.writeNull();
			return;
		}
		
		if (writer.existRef(obj)) { // 防止循环引用，此处会影响一些性能
			writer.writeNull();
			return;
		}
		
		writer.pushRef(obj);

		Class<?> clazz = obj.getClass();
		writer.append(OBJ_PRE);
		JsonObjMetaInfo[] jsonObjMetaInfos = classCache.get(clazz);
		if (jsonObjMetaInfos == null) {
			jsonObjMetaInfos = EncodeCompiler.compile(obj, clazz);
		}
		
		boolean first = true;
		for(JsonObjMetaInfo metaInfo : jsonObjMetaInfos){
			if(!first) writer.append(SEPARATOR);
			writer.write(metaInfo.getPropertyName());
			metaInfo.toJson(obj, writer);
			if(first) first = false;
		}
		writer.append(OBJ_SUF);

		writer.popRef();
	}

}
