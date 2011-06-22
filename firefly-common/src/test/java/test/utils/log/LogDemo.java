package test.utils.log;

import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class LogDemo {

	private static final Log log = LogFactory.getInstance().getLog(
			"firefly-common");
	private static final Log log2 = LogFactory.getInstance().getLog(
			"test-TRACE");
	private static final Log log3 = LogFactory.getInstance().getLog(
			"test-DEBUG");
	private static final Log log4 = LogFactory.getInstance().getLog(
			"test-ERROR");
	private static final Log log5 = LogFactory.getInstance().getLog(
	"test-WARN");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			log.info("test {} aa {}", "log1", 2);
			log.info("test {} bb {}", "log1", 2);
			log.info("test {} cc {}", "log1", 2);
			log.debug("cccc");
			log.warn("warn hello");

			log2.trace("log2 {} dfdfdf", 3, 5);
			log2.debug("cccc");

			log3.debug("log3", "dfd");
			log3.info("ccccddd");

			log4.error("log4");
			log4.warn("ccc");

			log5.warn("log5 {} {}", "warn");
			log5.error("log5 {}", "error");
			log5.trace("ccsc");

		} finally {
			LogFactory.getInstance().shutdown();
		}
	}

}
