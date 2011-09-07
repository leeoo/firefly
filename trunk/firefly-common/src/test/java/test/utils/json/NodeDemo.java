package test.utils.json;

import java.util.Date;
import com.firefly.utils.json.Json;

public class NodeDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Node node = new Node();
		Node node2 = new Node();
		
		node.setNode(node2);
		node.setId(33);
		node.setTimestamp(new Date());
		node.setSex('e');
		node.setText("dfs\t");
		int[] rig = new int[]{1,2,3};
		node.setRig(rig);
		
		node2.setNode(node);
		node2.setId(13);
		node2.setSex('f');
		node2.setFlag(true);
		
		System.out.println(Json.toJson(node));
//		StringWriter writer = new StringWriter();
//		Json.toJson(node, writer);
//		System.out.println(writer.toString());

	}

}
