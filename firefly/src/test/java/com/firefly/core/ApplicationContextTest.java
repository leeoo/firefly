package com.firefly.core;

import junit.framework.TestCase;

import org.junit.Test;

public class ApplicationContextTest extends TestCase {

	@Test
	public void testGetBean(){
		ApplicationContext applicationContext = new FileSystemXmlApplicationContext("firefly.xml");
		
		PersonService personService = (PersonService)applicationContext.getBean("personService");
		personService.info();
		Person person = (Person)applicationContext.getBean("person");
		System.out.println(person.getName());
	}
}
