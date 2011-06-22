package test.utils.log;

import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class LogUtils {

	private static final Log log = LogFactory.getInstance().getLog(
			"firefly-common");

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			log.info("test {} ddf {}", 1, 2);
			log.info("test {} ccc {}", 1, 2);
			log.info("test {} ddd {}", 1, 2);
			log.debug("cccc");
			log.warn("warn hello");
		} finally {
//			System.exit(0);
		}
	}

}
