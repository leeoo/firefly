package com.firefly.template.parser;

import com.firefly.utils.VerifyUtils;

public class StatementIf implements Statement {

	@Override
	public void parse(String content, JavaFileBuilder javaFileBuilder) {
		writePrefix(javaFileBuilder);
		content = content.trim();
		if (parseBoolean(content, javaFileBuilder))
			javaFileBuilder.writeBooleanObj(content.substring(2,
					content.length() - 1));
		javaFileBuilder.write("){\n");
		javaFileBuilder.getPreBlank().append('\t');
	}

	protected void writePrefix(JavaFileBuilder javaFileBuilder) {
		javaFileBuilder.write(javaFileBuilder.getPreBlank() + "if (");
	}

	private boolean parseBoolean(String content, JavaFileBuilder javaFileBuilder) {
		StringBuilder pre = new StringBuilder();
		String left = null, right = null;

		for (int i = 0; i < content.length(); i++) {
			switch (content.charAt(i)) {
			case '>':
			case '<':
				left = pre.toString().trim();
				right = content.charAt(i + 1) == '=' ? content.substring(i + 2)
						.trim() : content.substring(i + 1).trim();
				String symbol = content.charAt(i + 1) == '=' ? " "
						+ String.valueOf(content.charAt(i)) + "= " : " "
						+ String.valueOf(content.charAt(i)) + " ";
				parseNotEq(left, right, symbol, javaFileBuilder);
				return false;
			case '=':
				left = pre.toString().trim();
				right = content.charAt(i + 1) == '=' ? content.substring(i + 2)
						.trim() : content.substring(i + 1).trim();
				parseEq(left, right, javaFileBuilder);
				return false;
			default:
				pre.append(content.charAt(i));
			}
		}
		return true;
	}

	private void parseEq(String left, String right,
			JavaFileBuilder javaFileBuilder) {
		if (left.charAt(0) == '$') {
			left = left.substring(2, left.length() - 1);
			javaFileBuilder.writeObjNav(left).write(".equals(").write(right)
					.write(")");
		} else {
			right = right.substring(2, right.length() - 1);
			javaFileBuilder.write(left).write(".equals(").writeObjNav(right)
					.write(")");
		}
	}

	private void parseNotEq(String left, String right, String symbol,
			JavaFileBuilder javaFileBuilder) {
		if (left.charAt(0) == '$') {
			left = left.substring(2, left.length() - 1);
			if (VerifyUtils.isNumeric(right)) {
				javaFileBuilder.writeLongObj(left).write(symbol).write(right);
			} else if (VerifyUtils.isDouble(right)) {
				javaFileBuilder.writeDoubleObj(left).write(symbol).write(right);
			}
		} else {
			right = right.substring(2, right.length() - 1);
			if (VerifyUtils.isNumeric(left)) {
				javaFileBuilder.write(left).write(symbol).writeLongObj(right);
			} else if (VerifyUtils.isDouble(left)) {
				javaFileBuilder.write(left).write(symbol).writeLongObj(right);
			}
		}
	}

}
