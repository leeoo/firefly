package test.ioc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.component.AddService;
import test.component.FieldInject;
import test.component.MethodInject;
import test.component2.MethodInject2;
import test.component3.Person;
import test.component3.PersonService;

import com.firefly.core.AnnotationApplicationContext;
import com.firefly.core.ApplicationContext;
import com.firefly.core.XmlApplicationContext;

public class TestIoc {
	private static Logger log = LoggerFactory.getLogger(TestIoc.class);
	public static ApplicationContext applicationContext = new AnnotationApplicationContext();
	public static ApplicationContext xmlApplicationContext = new XmlApplicationContext();
	
	@Test
	public void testFieldInject() {
		FieldInject fieldInject = applicationContext.getBean("fieldInject");
		Assert.assertThat(fieldInject.add(5, 4), is(9));
		Assert.assertThat(fieldInject.add2(5, 4), is(9));

		fieldInject = applicationContext.getBean(FieldInject.class);
		Assert.assertThat(fieldInject.add(5, 4), is(9));
		Assert.assertThat(fieldInject.add2(5, 4), is(9));
	}

	@Test
	public void testMethodInject() {
		MethodInject m = applicationContext.getBean("methodInject");
		Assert.assertThat(m.add(5, 4), is(9));
	}

	@Test
	public void testMethodInject2() {
		MethodInject2 m = applicationContext.getBean("methodInject2");
		Assert.assertThat(m.add(5, 5), is(10));
		Assert.assertThat(m.getNum(), is(3));
	}

	@Test
	public void testSingle() {
		AddService t = applicationContext.getBean("addService");
		t.getI();
		t.getI();
		Assert.assertThat(t.getI(), greaterThan(0));
	}
	
	@Test
	public void testXmlInject(){
		Person person = xmlApplicationContext.getBean("person");
		Assert.assertThat(person.getName(), is("Jack"));
		PersonService personService = xmlApplicationContext.getBean("personService");
		List<Object> l = personService.getTestList();
		Assert.assertThat(l.size(), greaterThan(0));
		int i = 0;
		for(Object p : l){
			if(p instanceof Person){
				person = (Person)p;
				i++;
				log.info(person.getName());
			}else{
				log.info(String.valueOf(p));
			}
		}
		Assert.assertThat(i, greaterThan(1));
	}
}
