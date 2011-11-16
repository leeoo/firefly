package com.firefly.template.parser;

import java.util.List;

import com.firefly.template.support.RPNUtils;
import com.firefly.template.support.RPNUtils.Fragment;

public class StatementExpression implements Statement {

	@Override
	public void parse(String content, JavaFileBuilder javaFileBuilder) {
		List<Fragment> list = RPNUtils.getReversePolishNotation(content);
		for(Fragment f : list) {
			
		}
	}

}
