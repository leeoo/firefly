package com.firefly.template.support;

import java.util.LinkedList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

public class CompileUtils {
	public static int compile(String path, String classPath, List<String> files) {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		List<String> params = new LinkedList<String>();
		params.add("-encoding");
		params.add("utf-8");
		params.add("-sourcepath");
		params.add(path);
		if(classPath != null) {
			params.add("-classpath");
			params.add(classPath);
		}
		params.addAll(files);
		return compiler.run(null, null, null, params.toArray(new String[0]));
	}
	
	public static void main(String[] args) {
		System.out.println(System.getProperties());
	}
}
