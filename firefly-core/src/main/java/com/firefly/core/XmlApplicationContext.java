package com.firefly.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.xml.XmlBeanDefinition;
import com.firefly.core.support.xml.XmlBeanReader;


public class XmlApplicationContext extends AbstractApplicationContext {

	protected List<BeanDefinition> beanDefinitions;
	
	public XmlApplicationContext() {
		this(null);
	}
	
	public XmlApplicationContext(String file) {
		beanDefinitions = getBeanReader(file);
		addObjToContext();
		inject();
	}
	
	protected List<BeanDefinition> getBeanReader(String file){
		return new XmlBeanReader(file).loadBeanDefinitions();
	}
	
	/**
	 * 增加Xml中定义的组件到ApplicationContext
	 */
	private void addObjToContext() {
		for(BeanDefinition beanDefinition : this.beanDefinitions){
			XmlBeanDefinition xmlBeanDefinition = (XmlBeanDefinition)beanDefinition;
			
			Class<?> clazz = null;
			Object obj = null;
			
			try {
				clazz = Class.forName(xmlBeanDefinition.getClassName());
				obj = clazz.newInstance();
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			
			map.put(xmlBeanDefinition.getId(), obj);
		}
	}
	
	/**
	 * 依赖注入
	 */
	private void inject() {
		
		for(BeanDefinition beanDefinition : this.beanDefinitions){
			XmlBeanDefinition xmlBeanDefinition = (XmlBeanDefinition)beanDefinition;
			
			// 取得需要注入的对象
			Object obj = map.get(xmlBeanDefinition.getId());
			
			// 取得对象所有的属性
			Map<String, Object> properties = xmlBeanDefinition.getProperties();
			
			Class<?> clazz = obj.getClass();
			
			Method[] methods = clazz.getMethods();
			// 遍历所有属性依次注入
			for(Entry<String, Object> entry : properties.entrySet()){
				String key = entry.getKey();
				Object value = entry.getValue();
				
				// 遍历所有方法
				for(Method m : methods){
					// 寻找只有一个参数的setter方法
					String methodName = m.getName();
					Class<?>[] argsType = m.getParameterTypes();
					if(methodName.startsWith("set") && argsType.length == 1){
						methodName = methodName.substring(3,methodName.length());
						if(methodName.equalsIgnoreCase(key)){
							try {
								setFieldValue(argsType[0].getName(),(String)value,m,obj);
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
		}
	}
	
	private void setFieldValue(String argsType, String value, Method m,
			Object obj) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		
		if (argsType.equals("byte"))
			m.invoke(obj, Byte.parseByte(value));
		else if (argsType.equals("short"))
			m.invoke(obj, Short.parseShort(value));
		else if (argsType.equals("int"))
			m.invoke(obj, Integer.parseInt(value));
		else if (argsType.equals("long"))
			m.invoke(obj, Long.parseLong(value));
		else if (argsType.equals("float"))
			m.invoke(obj, Float.parseFloat(value));
		else if (argsType.equals("double"))
			m.invoke(obj, Double.parseDouble(value));
		else if (argsType.equals("boolean"))
			m.invoke(obj, Boolean.parseBoolean(value));
		else if (argsType.equals("java.lang.Byte"))
			m.invoke(obj, new Byte(value));
		else if (argsType.equals("java.lang.Short"))
			m.invoke(obj, new Short(value));
		else if (argsType.equals("java.lang.Integer"))
			m.invoke(obj, new Integer(value));
		else if (argsType.equals("java.lang.Long"))
			m.invoke(obj, new Long(value));
		else if (argsType.equals("java.lang.Float"))
			m.invoke(obj, new Float(value));
		else if (argsType.equals("java.lang.Double"))
			m.invoke(obj, new Double(value));
		else if (argsType.equals("java.lang.String"))
			m.invoke(obj, new String(value));
		else if (argsType.equals("java.lang.Boolean"))
			m.invoke(obj, new Boolean(value));
		else
			m.invoke(obj, map.get(value));
	}
}
