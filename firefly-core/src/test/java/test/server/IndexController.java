package test.server;

import java.io.IOException;

import javax.servlet.http.Cookie;
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
		response.addCookie(new Cookie("test", "cookie_value"));
		Cookie cookie = new Cookie("myname", "xiaoqiu");
		cookie.setMaxAge(5 * 60);
		response.addCookie(cookie);
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
		return null;
	}

	@RequestMapping(value = "/index3")
	public String index3(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		response.sendRedirect(request.getContextPath()
				+ request.getServletPath() + "/index");
		return null;
	}

	@RequestMapping(value = "/index4", view = View.REDIRECT)
	public String index4(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		return "/index";
	}

}
