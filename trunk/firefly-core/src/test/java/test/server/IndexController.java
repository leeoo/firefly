package test.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.Controller;
import com.firefly.annotation.RequestMapping;

@Controller
public class IndexController {

	@RequestMapping(value = "/index")
	public String index(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("hello", "welcome");
		System.out.println("index");
		return "/index.html";
	}
	
	@RequestMapping(value = "/index2")
	public String index2(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect("index");
		System.out.println("index2");
		return null;
	}

}
