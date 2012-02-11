package test.server;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.annotation.Controller;
import com.firefly.annotation.RequestMapping;
import com.firefly.mvc.web.View;

@Controller
public class IndexController {

	@RequestMapping(value = "/index")
	public String index(HttpServletRequest request, HttpServletResponse response) {
		request.setAttribute("hello", "welcome");
		System.out.println("index");
		return "/index.html";
	}

	@RequestMapping(value = "/test")
	public String test(HttpServletRequest request, HttpServletResponse response) {
		return "/test.html";
	}

	@RequestMapping(value = "/index2")
	public String index2(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.sendRedirect("index");
		System.out.println("index2");
		return null;
	}

	@RequestMapping(value = "/index3")
	public String index3(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.sendRedirect(request.getContextPath()
				+ request.getServletPath() + "/index");
		System.out.println("index3");
		return null;
	}

	@RequestMapping(value = "/index4", view = View.REDIRECT)
	public String index4(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		System.out.println("index4");
		return "/index";
	}

}
