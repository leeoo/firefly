package test;

import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.firefly.template.ObjectNavigator;


public class TestObjNavigator {
	
	@Test
	public void testRoot() {
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("ccc", "ddd");
		map2.put("eee", "fff");
		
		int[] arr = {111, 222, 333};
		
		List<String> list = new ArrayList<String>();
		list.add("list111");
		list.add("list222");
		list.add("list333");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("a", "fffff");
		map.put("b", map2);
		map.put("arr", arr);
		map.put("list", list);
		
		Assert.assertThat(ObjectNavigator.getInstance().find(map, "a").toString(), is("fffff"));
		Assert.assertThat(ObjectNavigator.getInstance().find(map, "b['ccc']").toString(), is("ddd"));
		Assert.assertThat(ObjectNavigator.getInstance().find(map, "b['eee']").toString(), is("fff"));
		Assert.assertThat(ObjectNavigator.getInstance().find(map, "b[\"ccc\"]").toString(), is("ddd"));
		Assert.assertThat((Integer)ObjectNavigator.getInstance().find(map, "arr[2]"), is(333));
		Assert.assertThat(ObjectNavigator.getInstance().find(map, "list[2]").toString(), is("list333"));
	}
	
	@Test
	public void testObject() {
		Foo foo = new Foo();
		Bar bar = new Bar();
		bar.setInfo("bar1");
		bar.setSerialNumber(33L);
		bar.setPrice(3.30);
		foo.setBar(bar);
		
		
		Map<String, Bar> fooMap = new HashMap<String, Bar>();
		bar = new Bar();
		bar.setInfo("bar2");
		bar.setSerialNumber(23L);
		bar.setPrice(2.30);
		fooMap.put("bar2", bar);
		foo.setMap(fooMap);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("foo", foo);
		
		ObjectNavigator o = ObjectNavigator.getInstance();
		Assert.assertThat(String.valueOf(o.find(map, "foo.bar.info")), is("bar1"));
		Assert.assertThat(String.valueOf(o.find(map, "foo.bar.serialNumber")), is("33"));
		Assert.assertThat(String.valueOf(o.find(map, "foo.bar.price")), is("3.3"));
		Assert.assertThat(String.valueOf(o.find(map, "foo.numbers[2]")), is("5"));
		Assert.assertThat(String.valueOf(o.find(map, "foo.map['bar2'].price")), is("2.3"));
	}
	
	public static void main(String[] args) {
		Foo foo = new Foo();
		Bar bar = new Bar();
		bar.setInfo("bar1");
		bar.setSerialNumber(33L);
		bar.setPrice(3.30);
		foo.setBar(bar);
		
		
		Map<String, Bar> fooMap = new HashMap<String, Bar>();
		bar = new Bar();
		bar.setInfo("bar2");
		bar.setSerialNumber(23L);
		bar.setPrice(2.30);
		fooMap.put("bar2", bar);
		foo.setMap(fooMap);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("foo", foo);
		
		System.out.println(ObjectNavigator.getInstance().find(map, "foo.bar.info"));
		System.out.println(ObjectNavigator.getInstance().find(map, "foo.bar.info"));
		System.out.println(ObjectNavigator.getInstance().find(map, "foo.bar.serialNumber"));
		System.out.println(ObjectNavigator.getInstance().find(map, "foo.bar.price"));
		System.out.println(ObjectNavigator.getInstance().find(map, "foo.numbers[2]"));
		System.out.println(ObjectNavigator.getInstance().find(map, "foo.map['bar2']"));
		System.out.println(ObjectNavigator.getInstance().find(map, "foo.map['bar2'].price"));
		
	}
}
