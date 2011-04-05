package test.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.firefly.annotation.Interceptor;

@Interceptor(uri = "/*/view*", order = 2)
public class FoodInterceptor3 {
	private static Logger log = LoggerFactory.getLogger(FoodInterceptor3.class);

	public void before(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("into", "2");
		log.info("before 2 [{}]", request.getRequestURI());
	}

	public void after(HttpServletRequest request, HttpServletResponse response) {
		log.info("after 2 [{}]", request.getRequestURI());
		
	}
}
