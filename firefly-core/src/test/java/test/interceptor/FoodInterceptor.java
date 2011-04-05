package test.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import test.mixed.Food;
import test.mixed.FoodService;

import com.firefly.annotation.Inject;
import com.firefly.annotation.Interceptor;

@Interceptor(uri = "/food*")
public class FoodInterceptor {
	private static Logger log = LoggerFactory.getLogger(FoodInterceptor.class);
	@Inject
	private FoodService foodService;

	public String before(HttpServletRequest request, HttpServletResponse response) {
		log.info("before 0 [{}]", request.getRequestURI());
		String fruit = request.getParameter("fruit");
		Food food = foodService.getFood(fruit);
		if(food != null) {
			request.setAttribute("fruit", food);
			return "/food.jsp";
		} else 
			return null;
	}

	public void after(HttpServletRequest request, HttpServletResponse response) {
		log.info("after 0 [{}]", request.getRequestURI());
	}
}
