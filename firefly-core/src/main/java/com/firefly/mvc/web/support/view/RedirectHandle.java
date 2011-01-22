package com.firefly.mvc.web.support.view;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.mvc.web.View;
import com.firefly.mvc.web.support.ViewHandle;

public class RedirectHandle implements ViewHandle {

	private static Logger log = LoggerFactory.getLogger(RedirectHandle.class);

	private RedirectHandle() {

	}

	private static class RedirectHandleHolder {
		private static RedirectHandle instance = new RedirectHandle();
	}

	public static RedirectHandle getInstance() {
		return RedirectHandleHolder.instance;
	}

	@Override
	public void render(HttpServletRequest request,
			HttpServletResponse response, Object view) throws ServletException,
			IOException {
		log.debug("view [{}]", View.REDIRECT);
		if (view instanceof String && view != null) {
			response.sendRedirect(request.getContextPath()
					+ request.getServletPath() + view.toString());
		}
	}

}
