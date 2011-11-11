package com.firefly.template.parser;

import java.util.HashMap;
import java.util.Map;

public class StateMachine {
	private static Map<String, Statement> map = new HashMap<String, Statement>();
	
	static {
		map.put("#if", new StatementIf());
		map.put("#elseif", new StatementElseIf());
		map.put("#else", new StatementElse());
		map.put("#end", new StatementEnd());
	}

	public static void parse(String keyword, String content,
			JavaFileBuilder javaFileBuilder) {
		Statement statement = map.get(keyword);
		if (statement != null)
			statement.parse(content, javaFileBuilder);
	}
}
