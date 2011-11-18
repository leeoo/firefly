package com.firefly.template.parser;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import static com.firefly.template.support.RPNUtils.Type.*;
import com.firefly.template.support.RPNUtils;
import com.firefly.template.support.RPNUtils.Fragment;

public class StatementExpression implements Statement {

	@Override
	public void parse(String content, JavaFileBuilder javaFileBuilder) {
		List<Fragment> list = RPNUtils.getReversePolishNotation(content);
		Deque<Fragment> d = new LinkedList<Fragment>();
		for (Fragment f : list) {
			if (isSymbol(f.type)) {
				Fragment right = d.pop();
				Fragment left = d.pop();
				
				switch (f.type) {
				case ARITHMETIC_OPERATOR:
					break;
				case LOGICAL_OPERATOR:
					break;
				case ASSIGNMENT_OPERATOR:
					break;
				case ARITHMETIC_OR_LOGICAL_OPERATOR:
					break;
				case CONDITIONAL_OPERATOR:
					break;
				default:
					break;
				}
			} else {
				d.push(f);
			}
		}
	}

	private boolean isSymbol(RPNUtils.Type type) {
		return type == ARITHMETIC_OPERATOR || type == LOGICAL_OPERATOR
				|| type == ASSIGNMENT_OPERATOR
				|| type == ARITHMETIC_OR_LOGICAL_OPERATOR
				|| type == CONDITIONAL_OPERATOR;
	}

}
