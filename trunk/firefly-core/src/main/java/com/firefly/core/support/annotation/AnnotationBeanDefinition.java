package com.firefly.core.support.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import com.firefly.core.support.BeanDefinition;

public interface AnnotationBeanDefinition extends BeanDefinition {
	Set<String> getInterfaceNames();

	void setInterfaceNames(Set<String> names);

	List<Field> getInjectFields();

	void setInjectFields(List<Field> fields);

	List<Method> getInjectMethods();

	void setInjectMethods(List<Method> methods);

	Object getObject();

	void setObject(Object object);

}
