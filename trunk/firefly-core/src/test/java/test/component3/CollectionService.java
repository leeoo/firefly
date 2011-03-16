package test.component3;

import java.util.LinkedList;
import java.util.Set;

public class CollectionService {
	private LinkedList<Object> list;
	private Set<Integer> set;
	private String[] strArray;

	public String[] getStrArray() {
		return strArray;
	}

	public void setStrArray(String[] strArray) {
		this.strArray = strArray;
	}

	public LinkedList<Object> getList() {
		return list;
	}

	public void setList(LinkedList<Object> list) {
		this.list = list;
	}

	public Set<Integer> getSet() {
		return set;
	}

	public void setSet(Set<Integer> set) {
		this.set = set;
	}

}
