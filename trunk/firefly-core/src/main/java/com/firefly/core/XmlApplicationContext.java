package com.firefly.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.firefly.annotation.Inject;
import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.annotation.AnnotationBeanDefinition;
import com.firefly.core.support.annotation.AnnotationBeanReader;
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

	public XmlApplicationContext() {
		this(null);
	}

	public XmlApplicationContext(String file) {
		super(file);
	}

	@Override
	protected List<BeanDefinition> getBeanDefinitions(String file) {
		List<BeanDefinition> list1 = new AnnotationBeanReader(file)
				.loadBeanDefinitions();
		List<BeanDefinition> list2 = new XmlBeanReader(file)
				.loadBeanDefinitions();
		if (list1 != null && list2 != null) {
			//TODO 混合annotation和xml的BeanDefinition，还需要加入冲突检测
			// 1.id相同的抛异常
			// 2.className或者interfaceName相同，但其中一个没有定义id，抛异常
			// 3.className或者interfaceName相同，且都定义的id，需要保存备忘，按类型或者接口自动注入的时候抛异常
			log.debug("mixed bean");
			list1.addAll(list2);
			return list1;
		} else if (list1 != null) {
			log.debug("annotation bean");
			return list1;
		} else if (list2 != null) {
			log.debug("xml bean");
			return list2;
		}
		return null;
	}

	@Override
	protected Object inject(BeanDefinition beanDef) {
		if (beanDef instanceof XmlBeanDefinition)
			return xmlInject(beanDef);
		else if (beanDef instanceof AnnotationBeanDefinition)
			return annotationInject(beanDef);
		else
			return null;
	}

	/**
	 * xml注入方式
	 * @param beanDef
	 * @return
	 */
	private Object xmlInject(BeanDefinition beanDef) {
		XmlBeanDefinition beanDefinition = (XmlBeanDefinition) beanDef;
		// 取得需要注入的对象
		Object object = beanDefinition.getObject();

		// 取得对象所有的属性
		Map<String, Object> properties = beanDefinition.getProperties();

		Class<?> clazz = object.getClass();

		// 遍历所有注册的set方法注入
		for (Method method : ReflectUtils.getSetterMethods(clazz)) {
			String methodName = method.getName();
			String propertyName = Character.toLowerCase(methodName.charAt(3))
					+ methodName.substring(4);
			Object value = properties.get(propertyName);
			if (value != null) {
				try {
					method.invoke(object, getInjectArg(value, method));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		addObjectToContext(beanDefinition);
		return object;
	}

	/**
	 * 
	 * @param value
	 *            属性值的元信息
	 * @param method
	 *            该属性的set方法
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object getInjectArg(Object value, Method method) {
		if (value instanceof ManagedValue) { // value
			return getValueArg(value, method);
		} else if (value instanceof ManagedRef) { // ref
			return getRefArg(value, method);
		} else if (value instanceof ManagedList) { // list
			return getListArg(value, method);
		} else if (value instanceof ManagedArray) { // array
			return getArrayArg(value, method);
		} else
			return null;
	}

	private Object getValueArg(Object value, Method method) {
		ManagedValue managedValue = (ManagedValue) value;
		String typeName = VerifyUtils.isEmpty(managedValue.getTypeName()) ? method
				.getParameterTypes()[0].getName() : managedValue.getTypeName();
		log.debug("value type [{}]", typeName);
		return ConvertUtils.convert(managedValue.getValue(), typeName);
	}

	private Object getRefArg(Object value, Method method) {
		ManagedRef ref = (ManagedRef) value;
		Object instance = map.get(ref.getBeanName());
		if (instance == null) {
			BeanDefinition b = findBeanDefinition(ref.getBeanName());
			if (b != null)
				instance = inject(b);
		}
		return instance;
	}

	@SuppressWarnings("unchecked")
	private Object getListArg(Object value, Method method) {
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
	}

	@SuppressWarnings("unchecked")
	private Object getArrayArg(Object value, Method method) {
		log.debug("xml inject method [{}]", method.getName());
		Class<?> setterParamType = method.getParameterTypes()[0];
		ManagedArray<Object> values = (ManagedArray<Object>) value;
		Collection collection = new ArrayList();
		for (Object item : values) {
			Object listValue = getInjectArg(item, method);
			collection.add(listValue);
		}
		return ConvertUtils.convert(collection, setterParamType);
	}

	/**
	 * annotation 注入方式
	 * @param beanDef
	 * @return
	 */
	private Object annotationInject(BeanDefinition beanDef) {
		AnnotationBeanDefinition beanDefinition = (AnnotationBeanDefinition) beanDef;
		fieldInject(beanDefinition);
		methodInject(beanDefinition);
		addObjectToContext(beanDefinition);
		return beanDefinition.getObject();
	}

	private void fieldInject(AnnotationBeanDefinition beanDefinition) {
		Object object = beanDefinition.getObject();

		// 属性注入
		for (Field field : beanDefinition.getInjectFields()) {
			field.setAccessible(true);
			Class<?> clazz = field.getType();
			String id = field.getAnnotation(Inject.class).value();
			String key = VerifyUtils.isNotEmpty(id) ? id : clazz.getName();
			Object instance = map.get(key);
			if (instance == null) {
				BeanDefinition b = findBeanDefinition(key);
				if (b != null)
					instance = inject(b);
			}
			if (instance != null) {
				try {
					field.set(object, instance);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void methodInject(AnnotationBeanDefinition beanDefinition) {
		Object object = beanDefinition.getObject();

		// 从方法注入
		for (Method method : beanDefinition.getInjectMethods()) {
			method.setAccessible(true);
			Class<?>[] params = method.getParameterTypes();
			Object[] p = new Object[params.length];
			for (int i = 0; i < p.length; i++) {
				String key = params[i].getName();
				Object instance = map.get(key);
				if (instance != null) {
					p[i] = instance;
				} else {
					BeanDefinition b = findBeanDefinition(key);
					if (b != null)
						p[i] = inject(b);
				}
			}
			try {
				method.invoke(object, p);
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
