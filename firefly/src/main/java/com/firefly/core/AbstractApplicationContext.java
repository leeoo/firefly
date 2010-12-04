package com.firefly.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.BeanDefinitionReader;

/**
 * IOC容器的具体实现
 * @author 杰然不同
 * @date 2010-12-5
 * @Version 1.0
 */
public abstract class AbstractApplicationContext implements ApplicationContext {
	
	protected Map<String,BeanDefinition> beansDefinitionMap = new HashMap<String, BeanDefinition>();
	
	protected Map<String, Object> beansMap = new HashMap<String, Object>();
	
	protected BeanDefinitionReader reader;
	
	/**
	 * 创建Bean
	 * @Date 2010-11-30
	 * @param beanName
	 * @return 具体Bean实例
	 */
	protected Object createBean(String beanName) {
		Object beanobj = this.beansMap.get(beanName);
		if(beanobj != null)
			return beanobj;
		
		Class<?> clazz = null;
		Object obj = null;
		BeanDefinition beanDefinitionan = (BeanDefinition)beansDefinitionMap.get(beanName);
		if(beanDefinitionan != null){
			try {
				clazz = Class.forName(beanDefinitionan.getClassName());
				obj = clazz.newInstance();
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}else{
			
		}
		
		// 依赖注入
		setProperties(obj, beanDefinitionan.getProperties());
		beansMap.put(beanName, obj);
		return obj;
	}
	
	/**
	 * 获取Bean
	 */
	public Object getBean(String name){
		return createBean(name);
	}
	
	
	/**
	 * 使用set方法注入值
	 */
	private Object setProperties(Object obj, Map<String, Object> properties) {
		Class<?> clazz = obj.getClass();
		try {
			Method[] methods = clazz.getMethods(); 
			for(Entry<String, Object> entry : properties.entrySet()){
				String key = entry.getKey();
				Object value = entry.getValue();

				for(Method m : methods){
					// 取出所有set方法并且只有一个参数
					String methodName = m.getName();
					Class<?>[] argsType = m.getParameterTypes();
					if(methodName.startsWith("set") && argsType.length == 1){
						String tempName = methodName.substring(3, methodName.length()).toLowerCase();
						if(tempName.equals(key)){
							setFieldValue(argsType[0].getName(),(String)value,m,obj);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return obj;
	}
	
	private void setFieldValue(String className, String value, Method m,
			Object obj) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException {
		
		if (className.equals("byte"))
			m.invoke(obj, Byte.parseByte(value));
		else if (className.equals("short"))
			m.invoke(obj, Short.parseShort(value));
		else if (className.equals("int"))
			m.invoke(obj, Integer.parseInt(value));
		else if (className.equals("long"))
			m.invoke(obj, Long.parseLong(value));
		else if (className.equals("float"))
			m.invoke(obj, Float.parseFloat(value));
		else if (className.equals("double"))
			m.invoke(obj, Double.parseDouble(value));
		else if (className.equals("boolean"))
			m.invoke(obj, Boolean.parseBoolean(value));
		else if (className.equals("java.lang.Byte"))
			m.invoke(obj, new Byte(value));
		else if (className.equals("java.lang.Short"))
			m.invoke(obj, new Short(value));
		else if (className.equals("java.lang.Integer"))
			m.invoke(obj, new Integer(value));
		else if (className.equals("java.lang.Long"))
			m.invoke(obj, new Long(value));
		else if (className.equals("java.lang.Float"))
			m.invoke(obj, new Float(value));
		else if (className.equals("java.lang.Double"))
			m.invoke(obj, new Double(value));
		else if (className.equals("java.lang.String"))
			m.invoke(obj, new String(value));
		else if (className.equals("java.lang.Boolean"))
			m.invoke(obj, new Boolean(value));
		else
			m.invoke(obj, createBean(value));
	}
	
	/**
	 * IOC容器初始化入口
	 * @Date 2010-11-29
	 */
	public void refresh(){
		beansDefinitionMap = reader.loadBeanDefinitions();
	}
}
