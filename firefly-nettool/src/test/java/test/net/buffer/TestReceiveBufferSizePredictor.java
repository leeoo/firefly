package test.net.buffer;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.net.ReceiveBufferSizePredictor;
import com.firefly.net.buffer.AdaptiveReceiveBufferSizePredictor;
import com.firefly.net.buffer.FixedReceiveBufferSizePredictor;

import static org.hamcrest.Matchers.*;

public class TestReceiveBufferSizePredictor {

	private static Logger log = LoggerFactory.getLogger(TestReceiveBufferSizePredictor.class);

	@Test
	public void testAdaptive() {
		ReceiveBufferSizePredictor receiveBufferSizePredictor = new AdaptiveReceiveBufferSizePredictor();
		receiveBufferSizePredictor.previousReceiveBufferSize(960);
		receiveBufferSizePredictor.previousReceiveBufferSize(960);
		receiveBufferSizePredictor.previousReceiveBufferSize(960);
		log.debug("current buf size: " + receiveBufferSizePredictor.nextReceiveBufferSize());
		Assert.assertThat(receiveBufferSizePredictor.nextReceiveBufferSize(), is(1024));

		receiveBufferSizePredictor.previousReceiveBufferSize(1025);
		receiveBufferSizePredictor.previousReceiveBufferSize(1300);
		log.debug("current buf size: " + receiveBufferSizePredictor.nextReceiveBufferSize());
		Assert.assertThat(receiveBufferSizePredictor.nextReceiveBufferSize(), greaterThan(1024));

		receiveBufferSizePredictor.previousReceiveBufferSize(4000);
		log.debug("current buf size: " + receiveBufferSizePredictor.nextReceiveBufferSize());
		Assert.assertThat(receiveBufferSizePredictor.nextReceiveBufferSize(), greaterThan(2000));
	}

	@Test
	public void testFix() {
		ReceiveBufferSizePredictor receiveBufferSizePredictor = new FixedReceiveBufferSizePredictor(1024 * 8);
		receiveBufferSizePredictor.previousReceiveBufferSize(960);
		Assert.assertThat(receiveBufferSizePredictor.nextReceiveBufferSize(), is(1024 * 8));
		receiveBufferSizePredictor.previousReceiveBufferSize(40000);
		Assert.assertThat(receiveBufferSizePredictor.nextReceiveBufferSize(), is(1024 * 8));
	}

}
