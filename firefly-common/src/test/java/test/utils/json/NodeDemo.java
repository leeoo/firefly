package test.utils.json;

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
		node.setSex('e');
		node.setText("dfs\t");
		int[] rig = new int[]{1,2,3};
		node.setRig(rig);
		System.out.println(Json.toJson(node));

	}

}
