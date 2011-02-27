package test.json;

import com.firefly.utils.json.Json;

public class NodeDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Node node = new Node();
		node.setNode(node);
		node.setId(33);
		System.out.println(Json.toJson(node));

	}

}
