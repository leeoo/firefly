package com.firefly.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.xml.ManagedArray;
import com.firefly.core.support.xml.ManagedList;
import com.firefly.core.support.xml.ManagedRef;
import com.firefly.core.support.xml.ManagedValue;
import com.firefly.core.support.xml.XmlBeanDefinition;
import com.firefly.core.support.xml.XmlBeanReader;
import com.firefly.utils.ConvertUtils;
import com.firefly.utils.ReflectUtils;
import com.firefly.utils.VerifyUtils;

/**
 *
 * @author 须俊杰, alvinqiu
 *
 */
public class XmlApplicationContext extends AbstractApplicationContext {

	private static Logger log = LoggerFactory
			.getLogger(XmlApplicationContext.class);
	protected List<BeanDefinition> beanDefinitions;

	public XmlApplicationContext() {
		this(null);
	}

	public XmlApplicationContext(String file) {
		beanDefinitions = getBeanReader(file);
		addObjectToContext();
		inject();
	}

	protected List<BeanDefinition> getBeanReader(String file) {
		return new XmlBeanReader(file).loadBeanDefinitions();
	}

	/**
	 * 增加Xml中定义的组件到ApplicationContext
	 */
	private void addObjectToContext() {
		for (BeanDefinition beanDefinition : this.beanDefinitions) {
			// 增加声明的组件到 ApplicationContext
			Object object = beanDefinition.getObject();
			// 把id作为key
			String id = beanDefinition.getId();
			if (VerifyUtils.isNotEmpty(id))
				map.put(id, object);

			// 把类名作为key
			map.put(beanDefinition.getClassName(), object);

			// 把接口名作为key
			Set<String> keys = beanDefinition.getInterfaceNames();
			for (String k : keys) {
				map.put(k, object);
			}
		}
	}

	/**
	 * 依赖注入
	 */
	private void inject() {

		for (BeanDefinition beanDefinition : this.beanDefinitions) {
			XmlBeanDefinition xmlBeanDefinition = (XmlBeanDefinition) beanDefinition;

			// 取得需要注入的对象
			Object obj = xmlBeanDefinition.getObject();

			// 取得对象所有的属性
			Map<String, Object> properties = xmlBeanDefinition.getProperties();

			Class<?> clazz = obj.getClass();

			// 遍历所有注册的set方法注入
			for (Method method : ReflectUtils.getSetterMethods(clazz)) {
				String methodName = method.getName();
				String propertyName = Character.toLowerCase(methodName
						.charAt(3))
						+ methodName.substring(4);
				Object value = properties.get(propertyName);
				if (value != null) {
					try {
						method.invoke(obj, getInjectArg(value, method));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}

	}

	/**
	 *
	 * @param value
	 *            属性值的元信息
	 * @param method
	 *            该属性的set方法
	 * @return
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	private Object getInjectArg(Object value, Method method) {
		if (value instanceof ManagedValue) { // value
			ManagedValue managedValue = (ManagedValue) value;
			String typeName = VerifyUtils.isEmpty(managedValue.getTypeName()) ? method
					.getParameterTypes()[0].getName()
					: managedValue.getTypeName();
			log.debug("value type [{}]", typeName);
			return ConvertUtils.convert(managedValue.getValue(), typeName);
		} else if (value instanceof ManagedRef) { // ref
			ManagedRef ref = (ManagedRef) value;
			return map.get(ref.getBeanName());
		} else if (value instanceof ManagedList) { // list
			log.debug("xml inject method [{}]", method.getName());
			Class<?> setterParamType = method.getParameterTypes()[0];
			ManagedList<Object> values = (ManagedList<Object>) value;
			Collection collection = null;
			log.debug("setter param type [{}]", setterParamType.getName());

			if (VerifyUtils.isNotEmpty(values.getTypeName())) { // 指定了list的类型
				try {
					collection = (Collection) XmlApplicationContext.class
							.getClassLoader().loadClass(values.getTypeName())
							.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else { // 根据set方法参数类型获取list类型
				collection = ConvertUtils.getCollectionObj(setterParamType);
			}

			for (Object item : values) {
				Object listValue = getInjectArg(item, method);
				collection.add(listValue);
			}

			return collection;
		} else if (value instanceof ManagedArray) { // array
			log.debug("xml inject method [{}]", method.getName());
			Class<?> setterParamType = method.getParameterTypes()[0];
			ManagedArray<Object> values = (ManagedArray<Object>) value;
			Collection collection = new ArrayList();
			for (Object item : values) {
				Object listValue = getInjectArg(item, method);
				collection.add(listValue);
			}
			return ConvertUtils.convert(collection, setterParamType);
		} else
			return null;
	}
}