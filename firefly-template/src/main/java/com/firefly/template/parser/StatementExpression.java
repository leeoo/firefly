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

	}

	public String parse(String content) {
		List<Fragment> list = RPNUtils.getReversePolishNotation(content);
		Deque<Fragment> d = new LinkedList<Fragment>();
		for (Fragment f : list) {
			if (isSymbol(f.type)) {
				Fragment right = d.pop();
				Fragment left = d.pop();

				Fragment ret = new Fragment();
				switch (f.type) {
				case ARITHMETIC_OPERATOR:
					if (left.type == STRING || right.type == STRING) {
						ret.type = STRING;
						if (f.value.equals("+")) {
							left.value = left.type == VARIABLE ? getVariable(left.value)
									: left.value;
							right.value = right.type == VARIABLE ? getVariable(right.value)
									: right.value;
							if (left.value.charAt(0) == '"'
									&& left.value
											.indexOf("objNav.getValue(model ,\"") < 0
									&& right.value.charAt(0) == '"'
									&& right.value
											.indexOf("objNav.getValue(model ,\"") < 0)
								ret.value = "\""
										+ left.value.substring(1,
												left.value.length() - 1)
										+ right.value.substring(1,
												right.value.length() - 1)
										+ "\"";
							else
								ret.value = left.value + " + " + right.value;
						} else {
							throw new ExpressionError(
									"String only suport '+' operator.");
						}
					} else if (left.type == DOUBLE || right.type == DOUBLE) {
						ret.type = DOUBLE;
						char f0 = f.value.charAt(0);
						String s0 = " " + f.value + " ";
						if (left.type == VARIABLE || right.type == VARIABLE) {
							left.value = left.type == VARIABLE ? getVariable0(
									left.value, "Double") + s0 : left.value;
							right.value = right.type == VARIABLE ? s0
									+ getVariable0(right.value, "Double")
									: right.value;
							ret.value = f0 == '*' || f0 == '/' || f0 == '%' ? left.value
									+ right.value
									: "(" + left.value + right.value + ")";
						} else if (left.value.indexOf("objNav") >= 0
								|| right.value.indexOf("objNav") >= 0)
							ret.value = left.value + s0 + right.value;
						else {
							switch (f0) {
							case '+':
								ret.value = String.valueOf(Double
										.parseDouble(left.value)
										+ Double.parseDouble(right.value));
								break;
							case '-':
								ret.value = String.valueOf(Double
										.parseDouble(left.value)
										- Double.parseDouble(right.value));
								break;
							case '*':
								ret.value = String.valueOf(Double
										.parseDouble(left.value)
										* Double.parseDouble(right.value));
								break;
							case '/':
								ret.value = String.valueOf(Double
										.parseDouble(left.value)
										/ Double.parseDouble(right.value));
								break;
							case '%':
								ret.value = String.valueOf(Double
										.parseDouble(left.value)
										% Double.parseDouble(right.value));
								break;

							default:
								throw new ExpressionError(f.value
										+ "is illegal");
							}
						}
					} else if (left.type == FLOAT || right.type == FLOAT) {
						ret.type = FLOAT;
						char f0 = f.value.charAt(0);
						String s0 = " " + f.value + " ";
						if (left.type == VARIABLE || right.type == VARIABLE) {
							left.value = left.type == VARIABLE ? getVariable0(
									left.value, "Float") + s0 : left.value;
							right.value = right.type == VARIABLE ? s0
									+ getVariable0(right.value, "Float")
									: right.value;
							ret.value = f0 == '*' || f0 == '/' || f0 == '%' ? left.value
									+ right.value
									: "(" + left.value + right.value + ")";
						} else if (left.value.indexOf("objNav") >= 0
								|| right.value.indexOf("objNav") >= 0)
							ret.value = left.value + s0 + right.value;
						else {
							switch (f0) {
							case '+':
								ret.value = String.valueOf(Float
										.parseFloat(left.value)
										+ Float.parseFloat(right.value));
								break;
							case '-':
								ret.value = String.valueOf(Float
										.parseFloat(left.value)
										- Float.parseFloat(right.value));
								break;
							case '*':
								ret.value = String.valueOf(Float
										.parseFloat(left.value)
										* Float.parseFloat(right.value));
								break;
							case '/':
								ret.value = String.valueOf(Float
										.parseFloat(left.value)
										/ Float.parseFloat(right.value));
								break;
							case '%':
								ret.value = String.valueOf(Float
										.parseFloat(left.value)
										% Float.parseFloat(right.value));
								break;
							default:
								throw new ExpressionError(f.value
										+ "is illegal");
							}
						}
					} else if (left.type == LONG || right.type == LONG) {
						ret.type = LONG;
						char f0 = f.value.charAt(0);
						String s0 = " " + f.value + " ";
						if (left.type == VARIABLE || right.type == VARIABLE) {
							left.value = left.type == VARIABLE ? getVariable0(
									left.value, "Long") + s0 : left.value;
							right.value = right.type == VARIABLE ? s0
									+ getVariable0(right.value, "Long")
									: right.value;
							ret.value = f0 == '*' || f0 == '/' || f0 == '%' ? left.value
									+ right.value
									: "(" + left.value + right.value + ")";
						} else if (left.value.indexOf("objNav") >= 0
								|| right.value.indexOf("objNav") >= 0)
							ret.value = left.value + s0 + right.value;
						else {
							switch (f0) {
							case '+':
								ret.value = String.valueOf(Long
										.parseLong(left.value)
										+ Long.parseLong(right.value));
								break;
							case '-':
								ret.value = String.valueOf(Long
										.parseLong(left.value)
										- Long.parseLong(right.value));
								break;
							case '*':
								ret.value = String.valueOf(Long
										.parseLong(left.value)
										* Long.parseLong(right.value));
								break;
							case '/':
								ret.value = String.valueOf(Long
										.parseLong(left.value)
										/ Long.parseLong(right.value));
								break;
							case '%':
								ret.value = String.valueOf(Long
										.parseLong(left.value)
										% Long.parseLong(right.value));
								break;
							case '<':
								ret.value = String.valueOf(Long
										.parseLong(left.value) << Long
										.parseLong(right.value));
								break;
							case '>':
								if (f.value.length() == 3
										&& f.value.charAt(1) == '>'
										&& f.value.charAt(2) == '>') {
									ret.value = String.valueOf(Long
											.parseLong(left.value) >>> Long
											.parseLong(right.value));
								} else if (f.value.length() == 2
										&& f.value.charAt(1) == '>') {
									ret.value = String.valueOf(Long
											.parseLong(left.value) >> Long
											.parseLong(right.value));
								} else {
									throw new ExpressionError(f.value
											+ "is illegal");
								}
								break;
							case '^':
								ret.value = String.valueOf(Long
										.parseLong(left.value)
										^ Long.parseLong(right.value));
								break;
							default:
								throw new ExpressionError(f.value
										+ "is illegal");
							}
						}
					} else if (left.type == INTEGER || right.type == INTEGER) {
						ret.type = INTEGER;
						char f0 = f.value.charAt(0);
						String s0 = " " + f.value + " ";
						if (left.type == VARIABLE || right.type == VARIABLE) {
							left.value = left.type == VARIABLE ? getVariable0(
									left.value, "Integer") + s0 : left.value;
							right.value = right.type == VARIABLE ? s0
									+ getVariable0(right.value, "Integer")
									: right.value;
							ret.value = f0 == '*' || f0 == '/' || f0 == '%' ? left.value
									+ right.value
									: "(" + left.value + right.value + ")";
						} else if (left.value.indexOf("objNav") >= 0
								|| right.value.indexOf("objNav") >= 0)
							ret.value = left.value + s0 + right.value;
						else {
							switch (f0) {
							case '+':
								ret.value = String.valueOf(Integer
										.parseInt(left.value)
										+ Integer.parseInt(right.value));
								break;
							case '-':
								ret.value = String.valueOf(Integer
										.parseInt(left.value)
										- Integer.parseInt(right.value));
								break;
							case '*':
								ret.value = String.valueOf(Integer
										.parseInt(left.value)
										* Integer.parseInt(right.value));
								break;
							case '/':
								ret.value = String.valueOf(Integer
										.parseInt(left.value)
										/ Integer.parseInt(right.value));
								break;
							case '%':
								ret.value = String.valueOf(Integer
										.parseInt(left.value)
										% Integer.parseInt(right.value));
								break;
							case '<':
								ret.value = String.valueOf(Integer
										.parseInt(left.value) << Integer
										.parseInt(right.value));
								break;
							case '>':
								if (f.value.length() == 3
										&& f.value.charAt(1) == '>'
										&& f.value.charAt(2) == '>') {
									ret.value = String.valueOf(Integer
											.parseInt(left.value) >>> Integer
											.parseInt(right.value));
								} else if (f.value.length() == 2
										&& f.value.charAt(1) == '>') {
									ret.value = String.valueOf(Integer
											.parseInt(left.value) >> Integer
											.parseInt(right.value));
								} else {
									throw new ExpressionError(f.value
											+ "is illegal");
								}
								break;
							case '^':
								ret.value = String.valueOf(Integer
										.parseInt(left.value)
										^ Integer.parseInt(right.value));
								break;
							default:
								throw new ExpressionError(f.value
										+ "is illegal");
							}
						}
					} else {
						throw new ExpressionError(left.type + " and "
								+ right.type + " ​​can not do arithmetic.");
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
				d.push(ret);
			} else {
				d.push(f);
			}
		}
		if (d.size() != 1)
			throw new ExpressionError("RPN error: " + content);
		return d.pop().value;
	}

	private boolean isSymbol(RPNUtils.Type type) {
		return type == ARITHMETIC_OPERATOR || type == LOGICAL_OPERATOR
				|| type == ASSIGNMENT_OPERATOR
				|| type == ARITHMETIC_OR_LOGICAL_OPERATOR
				|| type == CONDITIONAL_OPERATOR;
	}

	private String getVariable(String var) {
		int start = var.indexOf("${") + 2;
		int end = var.indexOf('}');
		return "objNav.getValue(model ,\"" + var.substring(start, end) + "\")";
	}

	private String getVariable0(String var, String t) {
		StringBuilder ret = new StringBuilder();
		int start = var.indexOf("${") + 2;
		int end = var.indexOf('}');
		ret.append(var.substring(0, start - 2)).append(
				"objNav.get" + t + "(model ,\"" + var.substring(start, end)
						+ "\")");
		if (end < var.length() - 1)
			ret.append(var.substring(end + 1, var.length() - 1));
		return ret.toString();
	}

}
