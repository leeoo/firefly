package com.firefly.core.support.xml;

import java.util.ArrayList;

/**
 * list元素
 * @author 须俊杰
 * @date 2011-3-9
 */
@SuppressWarnings("serial")
public class ManagedList<T> extends ArrayList<T> {
	private String typeName;

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	
}
