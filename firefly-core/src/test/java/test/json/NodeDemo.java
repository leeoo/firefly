package test.json;

import java.util.Date;

import com.firefly.utils.json.Json;

public class NodeDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Node node = new Node();
		node.setNode(node);
		node.setId(33);
		node.setTimestamp(new Date());
		System.out.println(Json.toJson(node));

	}

}
