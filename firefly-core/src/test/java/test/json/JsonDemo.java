package test.json;

import com.firefly.utils.json.Json;

public class JsonDemo {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Group group = new Group();
		group.setId(0L);
		group.setName("admin");

		User guestUser = new User();
		guestUser.setId(2L);
		guestUser.setName("guest");

		User rootUser = new User();
		rootUser.setId(3L);
		rootUser.setName("root");

		group.getUsers().add(guestUser);
		group.getUsers().add(rootUser);

		String jsonString = null;
		long start = System.currentTimeMillis();
		for (int i = 0; i < 100000; i++) {
			jsonString = Json.toJson(group);
		}
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		System.out.println(jsonString);

	}

}
