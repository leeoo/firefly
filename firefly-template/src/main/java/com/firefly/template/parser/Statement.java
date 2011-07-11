package com.firefly.template.parser;

public interface Statement {
	void translate(String prefix, String el, JavaFileBuilder javaFileBuilder);
}
