package com.firefly.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.core.support.BeanDefinition;
import com.firefly.core.support.exception.BeanDefinitionParsingException;
import com.firefly.utils.VerifyUtils;

abstract public class AbstractApplicationContext implements ApplicationContext {

	private static Logger log = LoggerFactory
			.getLogger(AbstractApplicationContext.class);
	protected Map<String, Object> map = new HashMap<String, Object>();
	protected List<BeanDefinition> beanDefinitions;

	public AbstractApplicationContext() {
		this(null);
	}

	public AbstractApplicationContext(String file) {
		beanDefinitions = getBeanDefinitions(file);
		check(); //冲突检测
		addObjectToContext();
	}

	private void addObjectToContext() {
		for (BeanDefinition beanDefinition : beanDefinitions) {
			inject(beanDefinition);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(Class<T> clazz) {
		return (T) map.get(clazz.getName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(String id) {
		return (T) map.get(id);
	}

	protected void check() {
		// TODO 需要增加测试用例，第3个还需要实现
		// 1.id相同的抛异常
		// 2.className或者interfaceName相同，但其中一个没有定义id，抛异常
		// 3.className或者interfaceName相同，且都定义的id，需要保存备忘，按类型或者接口自动注入的时候抛异常
		for (BeanDefinition b1 : beanDefinitions) {
			for (BeanDefinition b2 : beanDefinitions) {
				// 同一个beanDefinition不需要比较
				if(b1 == b2)
					continue;
				
				if (VerifyUtils.isNotEmpty(b1.getId())
						&& VerifyUtils.isNotEmpty(b2.getId())
						&& b1.getId().equals(b2.getId())) {
					error("bean " + b1.getClassName() + " and bean "
							+ b2.getClassName() + " have duplicate id ");
				}

				if (b1.getClassName().equals(b2.getClassName())) {
					if (VerifyUtils.isEmpty(b1.getId())
							|| VerifyUtils.isEmpty(b2.getId())) {
						error("class " + b1.getClassName()
								+ " redundant definition");
					}
				}

				Set<String> i1 = b1.getInterfaceNames();
				Set<String> i2 = b2.getInterfaceNames();
				for (String iname1 : i1) {
					for (String iname2 : i2) {
						if (iname1.equals(iname2)) {
							if (VerifyUtils.isEmpty(b1.getId())
									|| VerifyUtils.isEmpty(b2.getId())) {
								error("class " + b1.getClassName()
										+ " redundant definition");
							}
						}
					}
				}
			}
		}
	}

	protected void addObjectToContext(BeanDefinition beanDefinition) {
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

	protected BeanDefinition findBeanDefinition(String key) {
		for (BeanDefinition beanDefinition : beanDefinitions) {
			if (key.equals(beanDefinition.getId())) {
				return beanDefinition;
			} else if (key.equals(beanDefinition.getClassName())) {
				return beanDefinition;
			} else {
				for (String interfaceName : beanDefinition.getInterfaceNames()) {
					if (key.equals(interfaceName))
						return beanDefinition;
				}
			}
		}
		return null;
	}

	/**
	 * 处理异常
	 * 
	 * @param msg
	 *            异常信息
	 */
	protected void error(String msg) {
		log.error(msg);
		throw new BeanDefinitionParsingException(msg);
	}

	abstract protected List<BeanDefinition> getBeanDefinitions(String file);

	abstract protected Object inject(BeanDefinition beanDef);

}
