package com.firefly.template.support;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class RPNUtils {
	
	/**
	 * 生成逆波兰表达式
	 * 符号优先级：
	 * 10: "*", "/", "%"
	 *	9: "+", "-" 
	 *	8: ">>", ">>>", "<<"
	 *	7: ">", "<", ">=", "<="
	 *	6: "==", "!="
	 *	5: "&"
	 *	4: "|"
	 *	3: "^"
	 *	2: "&&"
	 *	1: "||"
	 *	0: "=", "+=", "-=", "*=", "/=", "%=", "^=", "&=", "|=", "<<=", ">>=", ">>>=" //0
	 * @param content
	 * @return
	 */
	public static List<Fragment> getReversePolishNotation(String content) {
		StringBuilder pre = new StringBuilder();
		Deque<Fragment> symbolDeque = new LinkedList<Fragment>();
		List<Fragment> list = new LinkedList<Fragment>();
		Fragment f = null;
		char c, n, n1, n2;
		
		for (int i = 0; i < content.length(); i++) {
			switch (content.charAt(i)) {
			case '(':
				pre.delete(0, pre.length());
				
				f = new Fragment();
				f.symbol = true;
				f.priority = -1000;
				f.value = "(";
				symbolDeque.push(f);
				break;
			case '*':
			case '/':
			case '%':
				n = content.charAt(i + 1);
				if(n == '=') { // *= /= %=
					outValue(pre, list);
					f = new Fragment();
					f.symbol = true;
					f.priority = 0;
					f.value = String.valueOf(content.charAt(i)) + "=";
					outSymbol(f, symbolDeque, list);
					
					i++;
					break;
				}
				
				// * / %
				outValue(pre, list);
				f = new Fragment();
				f.symbol = true;
				f.priority = 10;
				f.value = String.valueOf(content.charAt(i));
				outSymbol(f, symbolDeque, list);
				break;
			case '+':
			case '-':
				n = content.charAt(i + 1);
				if(n == '=') { // += -=
					outValue(pre, list);
					f = new Fragment();
					f.symbol = true;
					f.priority = 0;
					f.value = String.valueOf(content.charAt(i)) + "=";
					outSymbol(f, symbolDeque, list);
					
					i++;
					break;
				}
				
				if(n == '+' || n == '-') {
					pre.append(content.charAt(i)).append(content.charAt(i + 1));
					
					i++;
					break;
				}
				
				// 正负号判断
				boolean s = false;
				String left0 = "*/%+-><=&|(^";
				if(i == 0) {
					s = true;
				} else {
					for(int j = i - 1; j >= 0; j--) {
						char ch = content.charAt(j);
						if(!Character.isWhitespace(ch) ) {
							if(left0.indexOf(ch) >= 0)
								s = true;
							break;
						}
					}
				}
				
				
				// + -
				if(s) {
					pre.append(content.charAt(i));
				} else {
					outValue(pre, list);
					f = new Fragment();
					f.symbol = true;
					f.priority = 9;
					f.value = String.valueOf(content.charAt(i));
					outSymbol(f, symbolDeque, list);
				}
				break;
				
			case '>':
			case '<':
				c = content.charAt(i);
				n = content.charAt(i + 1);
				
				if(c == '>' && n == '>' || 
					c == '<' && n == '<') {
					if(i + 2 < content.length()) {
						n1 = content.charAt(i + 2);
						if(n1 == '=') { // <<= >>=
							outValue(pre, list);
							f = new Fragment();
							f.symbol = true;
							f.priority = 0;
							f.value = String.valueOf(content.charAt(i)) + content.charAt(i + 1) + "=";
							outSymbol(f, symbolDeque, list);
							
							i += 2;
							break;
						}
					}
					
					// << >>
					outValue(pre, list);
					f = new Fragment();
					f.symbol = true;
					f.priority = 8;
					f.value = String.valueOf(content.charAt(i)) + content.charAt(i + 1);
					outSymbol(f, symbolDeque, list);
					
					i++;
					break;
				}
				
				if(i + 2 < content.length()) {
					n1 = content.charAt(i + 2);
					if(c == '>' && n == '>' && n1 == '>') { 
						n2 = content.charAt(i + 3);
						if(i + 3 < content.length()) {
							if(n2 == '=') { // >>>=
								outValue(pre, list);
								f = new Fragment();
								f.symbol = true;
								f.priority = 0;
								f.value = ">>>=";
								outSymbol(f, symbolDeque, list);
								
								i += 3;
								break;
							}
						}
						
						// >>>
						outValue(pre, list);
						f = new Fragment();
						f.symbol = true;
						f.priority = 8;
						f.value = ">>>";
						outSymbol(f, symbolDeque, list);
						
						i += 2;
						break;
					}
				}
				
				if(n == '=') { // <= >=
					outValue(pre, list);
					f = new Fragment();
					f.symbol = true;
					f.priority = 7;
					f.value = String.valueOf(content.charAt(i)) + "=";
					outSymbol(f, symbolDeque, list);
					
					i++;
					break;
				}
				
				// < >
				outValue(pre, list);
				f = new Fragment();
				f.symbol = true;
				f.priority = 7;
				f.value = String.valueOf(content.charAt(i));
				outSymbol(f, symbolDeque, list);
				break;
			
			case '=':
				n = content.charAt(i + 1);
				if(n == '=') { // ==
					outValue(pre, list);
					f = new Fragment();
					f.symbol = true;
					f.priority = 6;
					f.value = String.valueOf(content.charAt(i)) + "=";
					outSymbol(f, symbolDeque, list);
					
					i++;
					break;
				}
				
				// =
				outValue(pre, list);
				f = new Fragment();
				f.symbol = true;
				f.priority = 0;
				f.value = String.valueOf(content.charAt(i));
				outSymbol(f, symbolDeque, list);
				break;
			
			case '!':
				n = content.charAt(i + 1);
				if(n == '=') { // !=
					outValue(pre, list);
					f = new Fragment();
					f.symbol = true;
					f.priority = 6;
					f.value = String.valueOf(content.charAt(i)) + "=";
					outSymbol(f, symbolDeque, list);
					
					i++;
					break;
				}
				pre.append('!');
				break;
				
			case '&':
				n = content.charAt(i + 1);
				if(n == '&') { // &&
					outValue(pre, list);
					f = new Fragment();
					f.symbol = true;
					f.priority = 2;
					f.value = "&&";
					outSymbol(f, symbolDeque, list);
					
					i++;
					break;
				}
				
				if(n == '=') { // &=
					outValue(pre, list);
					f = new Fragment();
					f.symbol = true;
					f.priority = 0;
					f.value = "&=";
					outSymbol(f, symbolDeque, list);
					
					i++;
					break;
				}
				
				// &
				outValue(pre, list);
				f = new Fragment();
				f.symbol = true;
				f.priority = 5;
				f.value = "&";
				outSymbol(f, symbolDeque, list);
				break;
			case '|':
				n = content.charAt(i + 1);
				if(n == '|') { // ||
					outValue(pre, list);
					f = new Fragment();
					f.symbol = true;
					f.priority = 1;
					f.value = "||";
					outSymbol(f, symbolDeque, list);
					
					i++;
					break;
				}
				
				if(n == '=') { // |=
					outValue(pre, list);
					f = new Fragment();
					f.symbol = true;
					f.priority = 0;
					f.value = "|=";
					outSymbol(f, symbolDeque, list);
					
					i++;
					break;
				}
				
				// |
				outValue(pre, list);
				f = new Fragment();
				f.symbol = true;
				f.priority = 4;
				f.value = "|";
				outSymbol(f, symbolDeque, list);
				break;
			case '^':
				n = content.charAt(i + 1);
				if(n == '=') {// ^=
					outValue(pre, list);
					f = new Fragment();
					f.symbol = true;
					f.priority = 0;
					f.value = String.valueOf(content.charAt(i)) + "=";
					outSymbol(f, symbolDeque, list);
					
					i++;
					break;
				}
				
				// ^
				outValue(pre, list);
				f = new Fragment();
				f.symbol = true;
				f.priority = 3;
				f.value = "^";
				outSymbol(f, symbolDeque, list);
				break;
			case ')':
				outValue(pre, list);
				f = new Fragment();
				f.symbol = true;
				f.priority = -1000;
				f.value = ")";
				outSymbol(f, symbolDeque, list);
				break;
			default:
				pre.append(content.charAt(i));
				break;
			}
		}
		
		outValue(pre, list);
		while(!symbolDeque.isEmpty())
			list.add(symbolDeque.pop());
		
		return list;
	}
	
	private static void outValue(StringBuilder pre, List<Fragment> list) {
		String v = pre.toString().trim();
		if(v.length() > 0) {
			Fragment f= new Fragment();
			f.symbol = false;
			f.priority = -200;
			f.value = v;
			list.add(f);
		}
		pre.delete(0, pre.length());
	}
	
	private static void outSymbol(Fragment f, Deque<Fragment> symbolDeque, List<Fragment> list) {		
		if(f.value.equals(")")) {
			for(Fragment top = null; !symbolDeque.isEmpty() 
					&& !(top = symbolDeque.pop()).value.equals("("); ) {
				list.add(top);
			}
		} else {
			for(Fragment top = null; !symbolDeque.isEmpty() 
					&& (top = symbolDeque.peek()).priority >= f.priority; ) {
				list.add(top);
				symbolDeque.pop();
			}
			symbolDeque.push(f);
		}
	}
	
	public static class Fragment {
		int priority;
		String value;
		boolean symbol;
		
		public String toString() {
			return value;
		}
	}
}
