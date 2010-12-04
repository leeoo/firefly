package com.firefly.core;

public class PersonService {
	private Person person;

	public void info(){
		System.out.println("My name's "+person.getName()+" , I'm "+person.getAge()+" years old!");
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
