package com.firefly.template;

import java.io.Writer;

public interface View {
	void render(Model model, Writer writer);
}
