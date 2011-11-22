package com.firefly.template.parser;

public class StatementIf implements Statement {

	@Override
	public void parse(String content, JavaFileBuilder javaFileBuilder) {
		writePrefix(javaFileBuilder);
		content = content.trim();
		StateMachine.parse("#eval", content, javaFileBuilder);
		javaFileBuilder.write("){\n");
		javaFileBuilder.getPreBlank().append('\t');
	}

	protected void writePrefix(JavaFileBuilder javaFileBuilder) {
		javaFileBuilder.write(javaFileBuilder.getPreBlank() + "if (");
	}

}
