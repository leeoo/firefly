package test.ioc;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.component3.CollectionService;
import test.component3.Person;
import test.component3.PersonService;

import com.firefly.core.ApplicationContext;
import com.firefly.core.XmlApplicationContext;

public class TestXmlIoc {
	private static Logger log = LoggerFactory.getLogger(TestXmlIoc.class);
	public static ApplicationContext xmlApplicationContext = new XmlApplicationContext();

	@Test
	public void testXmlInject() {
		Person person = xmlApplicationContext.getBean("person");
		Assert.assertThat(person.getName(), is("Jack"));
		PersonService personService = xmlApplicationContext
				.getBean("personService");
		List<Object> l = personService.getTestList();
		Assert.assertThat(l.size(), greaterThan(0));
		int i = 0;
		for (Object p : l) {
			if (p instanceof Person) {
				person = (Person) p;
				i++;
				log.info(person.getName());
			} else {
				log.info(p.toString());
			}
		}
		Assert.assertThat(i, greaterThan(1));
	}

	@Test
	public void testXmlLinkedListInject() {
		// 注入的不仅仅是List
		CollectionService collectionService = xmlApplicationContext
				.getBean("collectionService");
		List<Object> list = collectionService.getList();
		Assert.assertThat(list.size(), greaterThan(0));

		// list的值也是list
//		collectionService = xmlApplicationContext.getBean("collectionService2");
//		list = collectionService.getList();
//		Assert.assertThat(list.size(), greaterThan(0));
	}
}
