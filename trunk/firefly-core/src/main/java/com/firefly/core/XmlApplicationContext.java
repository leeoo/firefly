package com.firefly.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.exception.BeanDefinitionParsingException;
import com.firefly.core.support.xml.ManagedList;
import com.firefly.core.support.xml.ManagedRef;
import com.firefly.core.support.xml.ManagedValue;
import com.firefly.core.support.xml.XmlBeanDefinition;
import com.firefly.core.support.xml.XmlBeanReader;
import com.firefly.utils.StringUtils;
import com.firefly.utils.VerifyUtils;


public class XmlApplicationContext extends AbstractApplicationContext {

	private static Logger log = LoggerFactory.getLogger(XmlApplicationContext.class);

	protected List<BeanDefinition> beanDefinitions;

	public static final String DEFAULT_TYPE = "java.lang.String";
	
	public XmlApplicationContext() {
		this(null);
	}

	public XmlApplicationContext(String file) {
		beanDefinitions = getBeanReader(file);
		addObjectToContext();
		inject();
	}

	protected List<BeanDefinition> getBeanReader(String file){
		return new XmlBeanReader(file).loadBeanDefinitions();
	}

	/**
	 * 增加Xml中定义的组件到ApplicationContext
	 */
	private void addObjectToContext() {
		for(BeanDefinition beanDefinition : this.beanDefinitions){
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
			for(BeanDefinition beanDefinition : this.beanDefinitions){
				XmlBeanDefinition xmlBeanDefinition = (XmlBeanDefinition)beanDefinition;

				// 取得需要注入的对象
				Object obj = map.get(xmlBeanDefinition.getId());

				// 取得对象所有的属性
				Map<String, Object> properties = xmlBeanDefinition.getProperties();

				Class<?> clazz = obj.getClass();

				// 遍历所有属性依次注入
				for(Entry<String, Object> entry : properties.entrySet()){
					String name = entry.getKey();
					Object value = entry.getValue();
					Method m = getSetterMethod(clazz,name);
					
					Object injestArg = getInjectArg(name,value,m,clazz);
					
					// 执行方法注入
					m.invoke(obj, injestArg);
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
	private Object getInjectArg(String name,Object value,Method m,Class<?> clazz){		
		try {
			if(value instanceof ManagedValue){
				ManagedValue managedValue = (ManagedValue)value;
				String typeName = null;
				if(m != null){
					typeName = (managedValue.getTypeName() == null ? 
							m.getParameterTypes()[0].getName() : 
								managedValue.getTypeName());
				}else{
					typeName = (managedValue.getTypeName() == null ? 
								DEFAULT_TYPE : 
									managedValue.getTypeName());
				}
				return getTypeObj(typeName, managedValue.getValue());
			}else if(value instanceof ManagedRef){
				ManagedRef ref = (ManagedRef)value;
				return getTypeObj(null, ref.getBeanName());
			}else if(value instanceof ManagedList){
				// 寻找list当中的泛型类型
				Field f = getFieldByName(clazz,name);
				Class<?> genericClazz = null;		// 泛型类型
				// 得到Generic的类型
				Type t = f.getGenericType();
				if(t instanceof ParameterizedType){ // 参数化类型，如 Collection<String>
					ParameterizedType pt = (ParameterizedType)t;
					genericClazz = (Class<?>)pt.getActualTypeArguments()[0];
				}
				
				// 获得xml里定义的泛型类型
				ManagedList<Object> values = (ManagedList<Object>)value;
				String typeName = values.getTypeName();

				if(StringUtils.hasText(typeName)){
					Class<?> c = XmlApplicationContext.class.getClassLoader().loadClass(typeName);
					if(!genericClazz.equals(c) && !genericClazz.equals(Object.class)){
						error(name+" type mismatch");
					}
				}
				List<Object> l = new ArrayList<Object>();
				for(int i = 0;i < values.size();++i){
					Object item = values.get(i);
					Object listValue = getInjectArg(null,item,null,clazz);
					l.add(listValue);
				}
				return l;
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 根据属性名寻找对应的setter方法
	 * @param clazz
	 * @param name 属性名
	 * @return
	 */
	private Method getSetterMethod(Class<?> clazz, String name){
		Method[] methods = clazz.getMethods();
		// 遍历所有方法
		for(Method m : methods){
			// 寻找只有一个参数的setter方法
			String methodName = m.getName();
			Class<?>[] argsType = m.getParameterTypes();
			if(methodName.startsWith("set") && argsType.length == 1){
				methodName = methodName.substring(3,methodName.length());
				if(methodName.equalsIgnoreCase(name)){
					return m;
				}
			}
		}
		return null;
	}
	
	/**
	 * 根据名称寻找对应的Field
	 * @param clazz
	 * @param name
	 * @return
	 */
	private Field getFieldByName(Class<?> clazz, String name){
		Field[] fs = clazz.getDeclaredFields();
		for(Field f : fs){
			if(f.getName().equals(name)){
				return f;
			}
		}
		return null;
	}
	
	private Object getTypeObj(String argsType, String value){

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
			else if ("java.lang.String".equals(argsType))
				return new String(value);
			else if ("java.lang.Boolean".equals(argsType))
				return new Boolean(value);
			else
				return map.get(value);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 处理异常
	 * @param msg 异常信息
	 */
	private void error(String msg){
		log.error(msg);
		throw new BeanDefinitionParsingException(msg);
	}
}
