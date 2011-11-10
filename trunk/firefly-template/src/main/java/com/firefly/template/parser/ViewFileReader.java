package com.firefly.template.parser;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.firefly.template.Config;
import com.firefly.template.exception.TemplateFileReadException;
import com.firefly.template.support.CompileUtils;
import com.firefly.utils.io.FileUtils;
import com.firefly.utils.io.LineReaderHandler;

public class ViewFileReader {
	private Config config;
	private List<String> javaFiles = new ArrayList<String>();
	private List<String> templateFiles = new ArrayList<String>();
	private List<String> classNames = new ArrayList<String>();

	public ViewFileReader(Config config) {
		this.config = config;
		if (init() != 0)
			throw new TemplateFileReadException("template file parse error");
	}

	private int init() {
		int ret = 0;
		File file = new File(config.getCompiledPath());
		if (!file.exists()) {
			file.mkdir();
		}
		read0(new File(config.getViewPath()));

		// System.out.println(javaFiles);
		// System.out.println(templateFiles);
		// System.out.println(classNames);
		ret = CompileUtils.compile(config.getCompiledPath(), javaFiles);
		return ret;
	}

	public List<String> getJavaFiles() {
		return javaFiles;
	}

	public List<String> getTemplateFiles() {
		return templateFiles;
	}

	public List<String> getClassNames() {
		return classNames;
	}

	public void setClassNames(List<String> classNames) {
		this.classNames = classNames;
	}

	private void read0(File file) {
		file.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					read0(f);
				} else if (f.getName().endsWith("." + config.getSuffix())) {
					parse(f);
				}
				return false;
			}
		});
	}

	private void parse(File f) {
		String name = f.getAbsolutePath().replace('\\', '/');
		templateFiles.add(name.substring(config.getViewPath().length() - 1));
		name = name.substring(config.getViewPath().length() - 1,
				name.length() - config.getSuffix().length()).replace('/', '_')
				+ "java";
		classNames.add(name.substring(0, name.length() - 5));
		javaFiles.add(config.getCompiledPath() + "/" + name);
		System.out.println("======= " + name + " =======");
		
		JavaFileBuilder javaFileBuilder = new JavaFileBuilder(
				config.getCompiledPath(), name, config);
		TemplateFileLineReaderHandler lineReaderHandler = new TemplateFileLineReaderHandler(javaFileBuilder);
		
		try {
			FileUtils.read(f, lineReaderHandler);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			lineReaderHandler.close();
		}
	}

	private void parseComment(String comment, JavaFileBuilder javaFileBuilder) {
		System.out.println(comment.length() + "|1|comment:\t" + comment);
	}

	private void parseText(String text, JavaFileBuilder javaFileBuilder) {
		int cursor = 0;
		for (int start, end; (start = text.indexOf("${", cursor)) != -1
				&& (end = text.indexOf("}", start)) != -1;) {
			javaFileBuilder.writeText(text.substring(cursor, start))
					.writeObject(text.substring(start + 2, end));
			cursor = end + 1;
		}
		javaFileBuilder.writeText(text.substring(cursor, text.length()));
	}
	
	private class TemplateFileLineReaderHandler implements LineReaderHandler, Closeable {
		JavaFileBuilder javaFileBuilder;
		StringBuilder text = new StringBuilder();
		StringBuilder comment = new StringBuilder();
		int status = 0;
		
		public TemplateFileLineReaderHandler(JavaFileBuilder javaFileBuilder) {
			this.javaFileBuilder = javaFileBuilder;
		}
		
		@Override
		public void close() {
			if (text.length() > 0) {
				parseText(text.toString(), javaFileBuilder);
			}

			javaFileBuilder.write("\t}\n\n").writeTail().write("}");
			javaFileBuilder.close();
		}

		@Override
		public void readline(String line, int num) {
			switch (status) {
			case 0:
				int i = line.indexOf("<!--");

				if (i >= 0) { // html注释开始
					text.append(line.substring(0, i).trim());
					if (text.length() > 0) {
						parseText(text.toString(), javaFileBuilder);
						text = new StringBuilder();
					}

					int j = line.indexOf("-->");
					if (j > i + 4) { // html注释结束
						assert comment.length() == 0;
						parseComment(line.substring(i + 4, j).trim(),
								javaFileBuilder);
					} else {
						status = 1;
						comment.append(line.substring(i + 4).trim()
								+ "\n");
					}
				} else {
					text.append(line.trim());
				}
				break;
			case 1:
				int j = line.indexOf("-->");
				if (j >= 0) { // html注释结束
					status = 0;
					comment.append(line.substring(0, j).trim());
					parseComment(comment.toString(), javaFileBuilder);
					comment = new StringBuilder();
				} else
					comment.append(line.trim() + "\n");
				break;
			}

		}
	}
}
