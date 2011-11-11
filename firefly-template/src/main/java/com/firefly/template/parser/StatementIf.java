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
				left = pre.toString().trim();
				right = content.charAt(i + 1) == '=' ? content.substring(i + 2)
						.trim() : content.substring(i + 1).trim();
				if (left.charAt(0) == '$') {
					left = left.substring(2, left.length() - 1);
					String symbol = content.charAt(i + 1) == '=' ? " >= "
							: " > ";
					parse0(left, right, symbol, javaFileBuilder);
				} else {
					right = right.substring(2, right.length() - 1);
					String symbol = content.charAt(i + 1) == '=' ? " <= "
							: " < ";
					parse0(right, left, symbol, javaFileBuilder);
				}
				return false;
			case '<':
				left = pre.toString().trim();
				right = content.charAt(i + 1) == '=' ? content.substring(i + 2)
						.trim() : content.substring(i + 1).trim();
				if (left.charAt(0) == '$') {
					left = left.substring(2, left.length() - 1);
					String symbol = content.charAt(i + 1) == '=' ? " <= "
							: " < ";
					parse0(left, right, symbol, javaFileBuilder);
				} else {
					right = right.substring(2, right.length() - 1);
					String symbol = content.charAt(i + 1) == '=' ? " >= "
							: " > ";
					parse0(right, left, symbol, javaFileBuilder);
				}
				return false;
			case '=':
				left = pre.toString().trim();
				right = content.charAt(i + 1) == '=' ? content.substring(i + 2)
						.trim() : content.substring(i + 1).trim();
				if (left.charAt(0) == '$') {
					left = left.substring(2, left.length() - 1);
					javaFileBuilder.writeObjNav(left).write(".equals(")
							.write(right).write(")");
				} else {
					right = right.substring(2, right.length() - 1);
					javaFileBuilder.writeObjNav(right).write(".equals(")
							.write(left).write(")");
				}

				return false;
			default:
				pre.append(content.charAt(i));
			}
		}
		return true;
	}

	private void parse0(String obj, String val, String symbol,
			JavaFileBuilder javaFileBuilder) {
		if (VerifyUtils.isNumeric(val)) {
			javaFileBuilder.writeLongObj(obj).write(symbol).write(val);
		} else if (VerifyUtils.isDouble(val)) {
			javaFileBuilder.writeDoubleObj(obj).write(symbol).write(val);
		}
	}

}
