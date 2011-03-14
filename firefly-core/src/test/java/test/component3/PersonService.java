package test.component3;

import java.util.List;

public class PersonService {
	private Person person;

	private List<Object> testList;
	
	public void info(){
		System.out.println("My name's "+person.getName()+" , I'm "+person.getAge()+" years old!");
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public void setTestList(List<Object> testList) {
		this.testList = testList;
	}

	public List<Object> getTestList() {
		return testList;
	}
}
