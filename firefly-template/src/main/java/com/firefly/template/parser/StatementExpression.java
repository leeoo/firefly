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
		Deque<Fragment> valDeque = new LinkedList<Fragment>();
		for(Fragment f : list) {
			if(f.symbol) {
				
			} else {
				
			}
		}
	}

}
