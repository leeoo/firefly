package com.firefly.core;

public class PersonServiceImpl {
	private Person person;

	public void info(){
		System.out.println("My name's "+person.getName()+" I'm "+person.getAge()+" years old!");
	}
	
	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
}
