package com.firefly.template.parser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

abstract public class Node {
	protected Node parent;
	protected List<Node> children = new LinkedList<Node>();
	protected Map<String, String> attributes = new HashMap<String, String>();
	protected boolean end;

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

	public void addChildren(Node node) {
		children.add(node);
	}

	public List<Node> getChildren() {
		return children;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public String getAttribute(String name) {
		return attributes.get(name);
	}

	public void addAttribute(String name, String value) {
		attributes.put(name, value);
	}

}
