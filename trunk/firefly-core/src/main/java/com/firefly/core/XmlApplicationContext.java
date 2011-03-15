package com.firefly.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.xml.ManagedList;
import com.firefly.core.support.xml.ManagedRef;
import com.firefly.core.support.xml.ManagedValue;
import com.firefly.core.support.xml.XmlBeanDefinition;
import com.firefly.core.support.xml.XmlBeanReader;
import com.firefly.utils.Cast;
import com.firefly.utils.ReflectUtils;
import com.firefly.utils.VerifyUtils;

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
						.charAt(3)) + methodName.substring(4);
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
	 * @param value 属性值的元信息
	 * @param method 该属性的set方法
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object getInjectArg(Object value, Method method) {
		if (value instanceof ManagedValue) { // value
			ManagedValue managedValue = (ManagedValue) value;
			String typeName = VerifyUtils.isEmpty(managedValue.getTypeName()) ? method
					.getParameterTypes()[0].getName() : managedValue
					.getTypeName();
			return Cast.convert(managedValue.getValue(), typeName);
		} else if (value instanceof ManagedRef) { // ref
			ManagedRef ref = (ManagedRef) value;
			return map.get(ref.getBeanName());
		} else if (value instanceof ManagedList) { // list
			log.debug("xml inject method [{}]", method.getName());
			Class<?> setterParamType = method.getParameterTypes()[0];
			ManagedList<Object> values = (ManagedList<Object>) value;

			Object list = getCollectionObj(setterParamType);
			log.debug("setter param type [{}]", setterParamType.getName());

			for (Object item : values) {
				Object listValue = getInjectArg(item, method);
				@SuppressWarnings("rawtypes")
				Collection collection = (Collection) list;
				collection.add(listValue);
			}
			return list;
		} else
			return null;
	}

	@SuppressWarnings("rawtypes")
	private Object getCollectionObj(Class<?> clazz) {
		if (clazz.isInterface()) {
			if (clazz.isAssignableFrom(List.class))
				return new ArrayList();
			else if (clazz.isAssignableFrom(Set.class))
				return new HashSet();
			else if (clazz.isAssignableFrom(Queue.class))
				return new ArrayDeque();
			else if (clazz.isAssignableFrom(SortedSet.class))
				return new TreeSet();
			else if (clazz.isAssignableFrom(BlockingQueue.class))
				return new LinkedBlockingDeque();
			else
				return null;
		} else {
			Object obj = null;
			try {
				obj = clazz.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return obj;
		}
	}

	/**
	 * 处理异常
	 * 
	 * @param msg
	 *            异常信息
	 */
//	private void error(String msg) {
//		log.error(msg);
//		throw new BeanDefinitionParsingException(msg);
//	}
}
