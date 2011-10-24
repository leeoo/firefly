package com.firefly.template.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.firefly.template.Config;

public class JavaFileBuilder {
	private String name;
	private BufferedWriter writer;
	private Config config;

	public JavaFileBuilder(Config config, String name) {
		this.config = config;
		this.name = name;
		File file = new File(config.getCompiledPath(), name);
		try {
			if (!file.exists())
				file.createNewFile();
			
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}
	
	public JavaFileBuilder append(String str) {
		try {
			writer.write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public void close() {
		try {
			if(writer != null)
				writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
