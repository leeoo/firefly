package test;

import java.util.HashMap;
import java.util.Map;

import com.firefly.template.Model;

public class ModelMock implements Model {
	
	private Map<String, Object> map = new HashMap<String, Object>();

	@Override
	public void put(String key, Object object) {
		map.put(key, object);
	}

	@Override
	public Object get(String key) {
		return map.get(key);
	}

}
