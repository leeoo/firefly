package com.firefly.template.view;

import java.io.Writer;

import com.firefly.template.Config;
import com.firefly.template.Model;
import com.firefly.template.View;

public abstract class AbstractView implements View {

	@Override
	public void render(Model model, Writer writer) {
		try {
			main(model, writer);
		} catch (Throwable e) {
			Config.LOG.error("view render error", e);
		}
	}

	abstract protected void main(Model model, Writer writer)
			throws Throwable;

}
