package test.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.mixed.Food;
import test.mixed.FoodService;

import com.firefly.annotation.Inject;
import com.firefly.annotation.Interceptor;

@Interceptor(uri = "/food/view*", order = 1)
public class FoodInterceptor2 {
	private static Logger log = LoggerFactory.getLogger(FoodInterceptor2.class);
	@Inject
	private FoodService foodService;

	public void before(HttpServletRequest request, HttpServletResponse response) {
		log.info("before 1 [{}]", request.getRequestURI());
	}

	public String after(HttpServletRequest request, HttpServletResponse response) {
		log.info("after 1 [{}]", request.getRequestURI());
		String fruit = request.getParameter("strawberry");
		if ("strawberry".equals(fruit)) {
			Food food = foodService.getFood("strawberry");
			request.setAttribute("fruit", food);
			return "/foodView.jsp";
		} else 
			return null;
	}
}
