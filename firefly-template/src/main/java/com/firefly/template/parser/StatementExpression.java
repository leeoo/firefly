package com.firefly.template.parser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StatementExpression implements Statement {
	
	private String[][] symbols = {
		{"*", "/", "%"}, 
		{"+", "-"}, 
		{">>", ">>>", "<<"},
		{">", "<", ">=", "<="},
		{"==", "!="},
		{"&"},
		{"|"},
		{"^"},
		{"&&"},
		{"||"},
		{"=", "+=", "-=", "*=", "/=", "%=", "^=", "&=", "|=", "<<=", ">>=", ">>>="}
	};
	
	private Set<String> symbolSet = new HashSet<String>(Arrays.asList(
			"(", 
			"*", "/", "%", 
			"+", "-", 
			">>", ">>>", "<<", 
			">", "<", ">=", "<=",
			"==", "!=", 
			"&",
			"|",
			"^",
			"&&",
			"||",
			"=", "+=", "-=", "*=", "/=", "%=", "^=", "&=", "|=", "<<=", ">>=", ">>>=",
			")"));

	@Override
	public void parse(String content, JavaFileBuilder javaFileBuilder) {
		
		
	}
	
	public int getPriority(String symbol) {
		for (int i = 0; i < symbols.length; i++) {
			for (int j = 0; j < symbols[i].length; j++) {
				if(symbols[i][j].equals(symbol))
					return i;
			}
		}
		return -1;
	}
	
	public boolean isSymbol(String symbol) {
		return symbolSet.contains(symbol);
	}
	
	public static void main(String[] args) {
		StatementExpression s = new StatementExpression();
		System.out.println(s.getPriority("&&"));
		System.out.println(s.getPriority("/"));
	}

}
