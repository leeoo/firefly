package com.firefly.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
import com.firefly.core.support.exception.BeanDefinitionParsingException;
import com.firefly.core.support.xml.ManagedList;
import com.firefly.core.support.xml.ManagedRef;
import com.firefly.core.support.xml.ManagedValue;
import com.firefly.core.support.xml.XmlBeanDefinition;
import com.firefly.core.support.xml.XmlBeanReader;
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
		try {
			for (BeanDefinition beanDefinition : this.beanDefinitions) {
				XmlBeanDefinition xmlBeanDefinition = (XmlBeanDefinition) beanDefinition;

				// 取得需要注入的对象
				Object obj = xmlBeanDefinition.getObject();

				// 取得对象所有的属性
				Map<String, Object> properties = xmlBeanDefinition
						.getProperties();

				Class<?> clazz = obj.getClass();

				// 遍历所有注册的set方法注入
				for (Method method : getSetterMethods(clazz)) {
					String methodName = method.getName();
					String propertyName = Character.toLowerCase(methodName
							.charAt(3))
							+ methodName.substring(4);
					Object value = properties.get(propertyName);
					if (value != null) {
						Object injestArg = getInjectArg(propertyName, value,
								method, clazz);
						method.invoke(obj, injestArg);
					}
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private Object getInjectArg(String propertyName, Object value,
			Method method, Class<?> clazz) {
		try {
			if (value instanceof ManagedValue) {
				ManagedValue managedValue = (ManagedValue) value;
				String typeName = VerifyUtils.isEmpty(managedValue
						.getTypeName()) ? method.getParameterTypes()[0]
						.getName() : managedValue.getTypeName();
				return getTypeObj(typeName, managedValue.getValue());
			} else if (value instanceof ManagedRef) {
				ManagedRef ref = (ManagedRef) value;
				return map.get(ref.getBeanName());
			} else if (value instanceof ManagedList) {
				log.debug("xml inject method [{}]", method.getName());
				Class<?> setterParamType = method.getParameterTypes()[0];
				ManagedList<Object> values = (ManagedList<Object>) value;

				if (!matchGenericClazz(setterParamType, propertyName)) {
					error(propertyName + " type mismatch");
				}

				Object list = getCollectionObj(setterParamType);
				log.debug("setter param type [{}]", setterParamType.getName());

				for (Object item : values) {
					Object listValue = getInjectArg(propertyName, item, method,
							clazz);
					Collection collection = (Collection) list;
					collection.add(listValue);
				}
				return list;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	private boolean matchGenericClazz(Class<?> clazz, String propertyName) {
		// TODO 还没有实现，还需要实现泛型判断匹配
		return true;
	}

	private Method[] getSetterMethods(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		List<Method> list = new ArrayList<Method>();
		for (Method method : methods) {
			method.setAccessible(true);
			String methodName = method.getName();
			if (!methodName.startsWith("set")
					|| Modifier.isStatic(method.getModifiers())
					|| !method.getReturnType().equals(Void.TYPE)
					|| method.getParameterTypes().length != 1) {
				continue;
			}
			list.add(method);
		}
		return list.toArray(new Method[0]);
	}

	@SuppressWarnings("unchecked")
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

	private Object getTypeObj(String argsType, String value) {
		try {
			if ("byte".equals(argsType))
				return Byte.parseByte(value);
			else if ("short".equals(argsType))
				return Short.parseShort(value);
			else if ("int".equals(argsType))
				return Integer.parseInt(value);
			else if ("long".equals(argsType))
				return Long.parseLong(value);
			else if ("float".equals(argsType))
				return Float.parseFloat(value);
			else if ("double".equals(argsType))
				return Double.parseDouble(value);
			else if ("boolean".equals(argsType))
				return Boolean.parseBoolean(value);
			else if ("java.lang.Byte".equals(argsType))
				return new Byte(value);
			else if ("java.lang.Short".equals(argsType))
				return new Short(value);
			else if ("java.lang.Integer".equals(argsType))
				return new Integer(value);
			else if ("java.lang.Long".equals(argsType))
				return new Long(value);
			else if ("java.lang.Float".equals(argsType))
				return new Float(value);
			else if ("java.lang.Double".equals(argsType))
				return new Double(value);
			else if ("java.lang.Boolean".equals(argsType))
				return new Boolean(value);
			else
				return value;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 处理异常
	 *
	 * @param msg
	 *            异常信息
	 */
	private void error(String msg) {
		log.error(msg);
		throw new BeanDefinitionParsingException(msg);
	}
}
