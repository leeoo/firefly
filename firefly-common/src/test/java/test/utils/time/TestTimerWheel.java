package test.utils.time;

import org.junit.Assert;
import org.junit.Test;
import com.firefly.utils.time.wheel.TimeWheel;
import static org.hamcrest.Matchers.*;

public class TestTimerWheel {
	@Test
	public void test() {
//		log.debug("test timer wheel");
		TimeWheel t = new TimeWheel();
		t.start();
		final long start = System.currentTimeMillis();
		t.add(1500, new Runnable() {

			@Override
			public void run() {
				long end = System.currentTimeMillis();
//				log.info("ttt1: " + (end - start));
				Assert.assertThat((end - start), greaterThanOrEqualTo(1500L));
			}
		});

		t.add(2500, new Runnable() {

			@Override
			public void run() {
				long end = System.currentTimeMillis();
//				log.info("ttt2: " + (end - start));
				Assert.assertThat((end - start), greaterThanOrEqualTo(2500L));
			}
		});
	}

	public static void main(String[] args) {
		new TestTimerWheel().test();
	}
}
