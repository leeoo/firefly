package com.firefly.utils.json.serializer;

import static com.firefly.utils.json.JsonStringSymbol.OBJ_PRE;
import static com.firefly.utils.json.JsonStringSymbol.OBJ_SUF;

import java.io.IOException;

import com.firefly.utils.json.Serializer;
import com.firefly.utils.json.compiler.EncodeCompiler;
import com.firefly.utils.json.support.JsonObjMetaInfo;
import com.firefly.utils.json.support.JsonStringWriter;

public class ObjectSerializer implements Serializer {
	
	private JsonObjMetaInfo[] jsonObjMetaInfos;
	private Class<?> clazz;
	
	public ObjectSerializer(Class<?> clazz) {
		this.clazz = clazz;
	}

	@Override
	public void convertTo(JsonStringWriter writer, Object obj) throws IOException {		
		if (writer.existRef(obj)) { // 防止循环引用，此处会影响一些性能
			writer.writeNull();
			return;
		}
		
		writer.pushRef(obj);
		
		if(jsonObjMetaInfos == null)
			jsonObjMetaInfos = EncodeCompiler.compile(clazz);

		writer.append(OBJ_PRE);
		for(JsonObjMetaInfo metaInfo : jsonObjMetaInfos){
			writer.write(metaInfo.getPropertyName());
			metaInfo.toJson(obj, writer);
		}
		writer.append(OBJ_SUF);

		writer.popRef();
	}

}
