package com.firefly.template.parser;

import static com.firefly.template.support.RPNUtils.Type.*;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.firefly.template.exception.ExpressionError;
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
					if (left.type == right.type) {
						Fragment f0 = new Fragment();
						f0.type = left.type;
						switch (left.type) {
						case VARIABLE:

							break;
						case INTEGER:
							f0.value = String.valueOf(Integer
									.parseInt(left.value)
									+ Integer.parseInt(right.value));
							break;
						case LONG:
							f0.value = String.valueOf(Long
									.parseLong(left.value)
									+ Long.parseLong(right.value));
							break;
						case FLOAT:
							f0.value = String.valueOf(Float
									.parseFloat(left.value)
									+ Float.parseFloat(right.value));
							break;
						case DOUBLE:
							f0.value = String.valueOf(Double
									.parseDouble(left.value)
									+ Double.parseDouble(right.value));
							break;
						case STRING:
							f0.value = left.value + right.value;
							break;
						case BOOLEAN:
							throw new ExpressionError(
									"Boolean values ​​can not do arithmetic.");
						}
						d.push(f0);
					} else if(left.type == VARIABLE) {
						
					} else if(right.type == VARIABLE) {
						
					} else if(left.type == STRING) {
						
					} else if(right.type == STRING) {
						
					}
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
