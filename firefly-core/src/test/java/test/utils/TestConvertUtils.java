package test.utils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import com.firefly.utils.ConvertUtils;

public class TestConvertUtils {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			ClassNotFoundException {
		Collection collection = new ArrayList();
		collection.add("arr1");
		collection.add("arr2");
		TestConvertUtils arrayUtils = new TestConvertUtils();
		Method method = TestConvertUtils.class.getMethod("setArray",
				String[].class);
		System.out.println(method.getName());
		Object obj = ConvertUtils.convert(collection,
				method.getParameterTypes()[0]);
		method.invoke(arrayUtils, obj);

		System.out.println(String[].class.getName());
		System.out.println(String.class.getName());

		Class<?> clazz = TestConvertUtils.class.getClassLoader().loadClass(
				"java.lang.String");
		obj = ConvertUtils.convert(collection,
				Array.newInstance(clazz.getComponentType(), 0).getClass());
		method.invoke(arrayUtils, obj);
	}

	public void setArray(String[] arr) {
		System.out.println(Arrays.toString(arr));
	}

}
