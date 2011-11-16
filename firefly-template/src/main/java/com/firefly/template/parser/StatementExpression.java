package com.firefly.template.parser;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.firefly.template.support.RPNUtils;
import com.firefly.template.support.RPNUtils.Fragment;

public class StatementExpression implements Statement {

	@Override
	public void parse(String content, JavaFileBuilder javaFileBuilder) {
		List<Fragment> list = RPNUtils.getReversePolishNotation(content);
		Deque<Fragment> d = new LinkedList<Fragment>();
		for(Fragment f : list) {
			if(f.symbol) {
				Fragment f1 = d.pop();
				Fragment f0 = d.pop();
				
				
			} else {
				d.push(f);
			}
		}
	}
	
	public static void main(String[] args) {
		System.out.println(Integer.parseInt("-3333"));
	}

}
