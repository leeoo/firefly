package com.firefly.mvc.web.support.view;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.mvc.web.servlet.SystemHtmlPage;
import com.firefly.mvc.web.support.ViewHandle;
import com.firefly.template.Model;
import com.firefly.template.TemplateFactory;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class FFTViewHandle implements ViewHandle {
	
	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	private TemplateFactory t;
	
	private FFTViewHandle() {
		
	}
	
	private static class Holder {
		private static FFTViewHandle instance = new FFTViewHandle();
	}
	
	public static FFTViewHandle getInstance() {
		return Holder.instance;
	}
	
	public FFTViewHandle init(String viewPath, String encoding) {
		com.firefly.template.Config config = new com.firefly.template.Config();
		config.setViewPath(viewPath);
		config.setCharset(encoding);
		// TODO 这里怎么取classpath
		config.setClassPath("F:/develop/apache-maven/maven_repository/com/firefly/firefly-template/1.0-SNAPSHOT/firefly-template-1.0-SNAPSHOT.jar");
		t = new TemplateFactory(config).init();
		return this;
	}

	@Override
	public void render(final HttpServletRequest request,
			HttpServletResponse response, Object view) throws ServletException,
			IOException {
		if (view instanceof String && view != null) {
			String ret = (String) view;
			log.debug("fft path [{}]", ret);
			com.firefly.template.View v = t.getView(ret);
			if(v == null) {
				SystemHtmlPage.scNotFound(request, response, t.getConfig().getCharset());
			} else {
				ServletOutputStream out = response.getOutputStream();
				Model model = new Model(){

					@SuppressWarnings("unchecked")
					@Override
					public void clear() {
						Enumeration<String> e = request.getAttributeNames();
						while(e.hasMoreElements()) {
							String name = e.nextElement();
							request.removeAttribute(name);
						}
					}

					@Override
					public Object get(String name) {
						return request.getAttribute(name);
					}

					@Override
					public void put(String name, Object o) {
						request.setAttribute(name, o);
					}

					@Override
					public void remove(String name) {
						request.removeAttribute(name);
					}};
				try {
					v.render(model, out);
				} finally {
					out.close();
				}
			}
		}
	}

}
