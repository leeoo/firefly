package test.utils.json;

import com.firefly.utils.json.Json;

public class BookDemo {
	public static void main(String[] args) {
		Book book = new Book();
		book.setPrice(10.0);
		book.setId(331);
		book.setText("very good");
		book.setSell(true);
		book.setTitle("gook book");
		System.out.println(Json.toJson(book));
	}
}
