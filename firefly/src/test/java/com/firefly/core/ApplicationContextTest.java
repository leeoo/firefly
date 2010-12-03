package com.firefly.core;

import junit.framework.TestCase;

import org.junit.Test;

public class ApplicationContextTest extends TestCase {

	@Test
	public void testGetBean(){
		ApplicationContext applicationContext = new FileSystemXmlApplicationContext("firefly.xml");
		
		PersonServiceImpl p = (PersonServiceImpl)applicationContext.getBean("personService");
		p.info();
		Person p1 = (Person)applicationContext.getBean("person");
		System.out.println(p1.getName());
	}
}
