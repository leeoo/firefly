package com.firefly.template.view;

import java.io.Writer;
import java.util.Map;
import com.firefly.template.Config;
import com.firefly.template.View;

public abstract class AbstractView implements View {

	@Override
	public void render(Map<String, Object> map, Writer writer) {
		try {
			main(map, writer);
		} catch (Throwable e) {
			Config.LOG.error("view render error", e);
		}
	}

	abstract protected void main(Map<String, Object> map, Writer writer)
			throws Throwable;

}
