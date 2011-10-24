package com.firefly.template.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JavaFileBuilder {
	private String name;
	private BufferedWriter writer;
	private boolean writeHead = false;
	private StringBuilder tail = new StringBuilder();
	private int textCount = 0;

	public JavaFileBuilder(String path, String name) {
		this.name = name;
		File file = new File(path, name);
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
	
	public JavaFileBuilder write(String str) {
		try {
			writeHead();
			writer.write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}
	
	public JavaFileBuilder appendTail(String str) {
		tail.append(str);
		return this;
	}
	
	public JavaFileBuilder writerText(String str) {
		write("\t\tout.write(_TEXT_" + textCount + ");\n")
		.appendTail("\tprivate static final byte[] _TEXT_" + textCount + " = new byte[]{" + str + "};\n");
		textCount++;
		return this;
	}

	public JavaFileBuilder writeTail() {
		try {
			writer.write(tail.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	private void writeHead() throws IOException {
		if (!writeHead) {
			writer.write("import java.io.OutputStream;\n");
			writer.write("import com.firefly.template.ObjectNavigator;\n");
			writer.write("import com.firefly.template.Model;\n");
			writer.write("import com.firefly.template.view.AbstractView;\n\n");

			String className = name.substring(0, name.length() - 5);

			writer.write("public class " + className
					+ " extends AbstractView {\n\n");
			writer.write("\tpublic static final " + className
					+ " INSTANCE = new " + className + "();\n\n");
			writer.write("\t@Override\n");
			writer.write("\tprotected void main(Model model, OutputStream out) throws Throwable {\n");

			writeHead = true;
		}
	}

	public void close() {
		try {
			if (writer != null)
				writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}