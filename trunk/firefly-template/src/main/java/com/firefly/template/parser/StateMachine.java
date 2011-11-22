package com.firefly.template.parser;

import java.util.HashMap;
import java.util.Map;

public class StateMachine {
	private static final Map<String, Statement> MAP = new HashMap<String, Statement>();
	
	static {
		MAP.put("#eval", new StatementExpression());
		MAP.put("#if", new StatementIf());
		MAP.put("#elseif", new StatementElseIf());
		MAP.put("#else", new StatementElse());
		MAP.put("#end", new StatementEnd());
	}

	public static void parse(String keyword, String content,
			JavaFileBuilder javaFileBuilder) {
		Statement statement = MAP.get(keyword);
		if (statement != null)
			statement.parse(content, javaFileBuilder);
	}
}
