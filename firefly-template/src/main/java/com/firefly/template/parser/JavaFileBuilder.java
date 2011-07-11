package com.firefly.template.parser;

import java.io.File;

import com.firefly.template.Config;

public class JavaFileBuilder {
	private String name;
	private String ext;
	private boolean main;
	private StringBuilder content;
	private Config config;

	public JavaFileBuilder(Config config) {
		this.config = config;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public boolean isMain() {
		return main;
	}

	public void setMain(boolean main) {
		this.main = main;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StringBuilder append(String string) {
		return content.append(string);
	}

	public void save() {
		File file = new File(config.getCompiledPath(), name);
		// TODO 
//		if(!file.exists())
	}
}
