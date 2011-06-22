package test.utils;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.firefly.utils.StringUtils;
import static org.hamcrest.Matchers.*;

public class TestStringUtils {

	@Test
	public void testReplace() {
		String str = "hello ${t1} and ${t2}";
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("t1", "foo");
		map.put("t2", "bar");
		String ret = StringUtils.replace(str, map);
		Assert.assertThat(ret, is("hello foo and bar"));

		map = new HashMap<String, Object>();
		map.put("t1", "foo");
		map.put("t2", "${dddd}");
		ret = StringUtils.replace(str, map);
		Assert.assertThat(ret, is("hello foo and ${dddd}"));

		map = new HashMap<String, Object>();
		map.put("t1", null);
		map.put("t2", "${dddd}");
		ret = StringUtils.replace(str, map);
		Assert.assertThat(ret, is("hello null and ${dddd}"));

		map = new HashMap<String, Object>();
		map.put("t1", 33);
		map.put("t2", 42L);
		ret = StringUtils.replace(str, map);
		Assert.assertThat(ret, is("hello 33 and 42"));
	}

	@Test
	public void testReplace2() {
		String str2 = "hello {{{{} and {}";
		String ret2 = StringUtils.replace(str2, "foo", "bar");
		Assert.assertThat(ret2, is("hello {{{foo and bar"));

		ret2 = StringUtils.replace(str2, "foo");
		Assert.assertThat(ret2, is("hello {{{foo and {}"));

		ret2 = StringUtils.replace(str2, "foo", "bar", "foo2");
		Assert.assertThat(ret2, is("hello {{{foo and bar"));

		ret2 = StringUtils.replace(str2, 12, 23L, 33);
		Assert.assertThat(ret2, is("hello {{{12 and 23"));
	}

	public static void main(String[] args) {
		String str = "hello ${t1} and ${t2}";
		Map<String, String> map = new HashMap<String, String>();
		map.put("t1", "foo");
		map.put("t2", "bar");
		String ret = StringUtils.replace(str, map);
		System.out.println(ret);

		map = new HashMap<String, String>();
		map.put("t1", "foo");
		map.put("t2", "${dddd}");
		ret = StringUtils.replace(str, map);
		System.out.println(ret);

		map = new HashMap<String, String>();
		map.put("t1", "foo");
		map.put("t2", null);
		ret = StringUtils.replace(str, map);
		System.out.println(ret);

		String str2 = "hello {{{{} and {}";
		String ret2 = StringUtils.replace(str2, "foo", "bar");
		System.out.println(ret2);

		ret2 = StringUtils.replace(str2, "foo");
		System.out.println(ret2);

		ret2 = StringUtils.replace(str2, "foo", "bar", "foo2");
		System.out.println(ret2);
	}
}
