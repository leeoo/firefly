package test.ioc;

import static org.hamcrest.Matchers.*;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.mixed.Food;
import test.mixed.FoodService;
import com.firefly.core.ApplicationContext;
import com.firefly.core.XmlApplicationContext;

public class TestMixIoc {
	private static Logger log = LoggerFactory.getLogger(TestMixIoc.class);
	public static ApplicationContext applicationContext = new XmlApplicationContext(
			"mixed-config.xml");

	@Test
	public void testInject() {
		FoodService foodService = applicationContext.getBean("foodService");
		Food food = foodService.getFood("apple");
		log.debug(food.getName());
		Assert.assertThat(food.getPrice(), is(5.3));
		food = foodService.getFood("strawberry");
		log.debug(food.getName());
		Assert.assertThat(food.getPrice(), is(10.00));
	}
}
