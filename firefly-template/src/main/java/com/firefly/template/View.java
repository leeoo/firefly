package com.firefly.template;

import java.io.Writer;
import java.util.Map;

public interface View {
	void render(Map<String, Object> map, Writer writer);
}
