package com.firefly.mvc.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.firefly.mvc.web.AnnotationWebContext;
import com.firefly.mvc.web.DispatcherController;
import com.firefly.mvc.web.WebContext;
import com.firefly.mvc.web.support.BeanHandle;
import com.firefly.mvc.web.support.MethodParam;
import com.firefly.mvc.web.support.ParamHandle;
import com.firefly.utils.VerifyUtils;

/**
 * 前端控制器
 * @author alvinqiu
 *
 */
public class HttpServletDispatcherController implements DispatcherController {

	private static Logger log = LoggerFactory
			.getLogger(HttpServletDispatcherController.class);

	private WebContext webContext;

	private HttpServletDispatcherController() {

	}

	private static class HttpServletDispatcherControllerHolder {
		private static HttpServletDispatcherController instance = new HttpServletDispatcherController();
	}

	public static HttpServletDispatcherController getInstance() {
		return HttpServletDispatcherControllerHolder.instance;
	}

	@Override
	public void dispatcher(HttpServletRequest request,
			HttpServletResponse response) {
		String encoding = webContext.getEncoding();
		try {
			request.setCharacterEncoding(encoding);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		response.setCharacterEncoding(encoding);

		String uri = request.getRequestURI();
		String prePath = request.getContextPath() + request.getServletPath();
		String invokeUri = uri.substring(prePath.length());
		String key = request.getMethod() + "@" + invokeUri;
		String beforeIntercept = "b#" + invokeUri;
		String afterIntercept = "a#" + invokeUri;
		Set<BeanHandle> beforeSet = webContext.getBean(beforeIntercept);
		Set<BeanHandle> afterSet = webContext.getBean(afterIntercept);

		log.debug("uri map [{}]", key);
		BeanHandle beanHandle = webContext.getBean(key);
		if (beanHandle != null) {
			Object ret = null;
			Object beforeRet = null; // 前置拦截器的返回值
			BeanHandle lastBefore = null; // 最后得到的前置拦截器
			Object afterRet = null; // 后置拦截器的返回值
			BeanHandle lastAfter = null; // 最后得到的后置拦截器

			// 前置拦截栈调用
			if (beforeSet != null) {
				for (BeanHandle before : beforeSet) {
					Object[] beforeP = getParams(request, response, before);
					beforeRet = before.invoke(beforeP);
					if (beforeRet != null) {
						lastBefore = before;
						break;
					}
				}
			}

			if (beforeRet == null) {
				// controller调用
				Object[] p = getParams(request, response, beanHandle);
				ret = beanHandle.invoke(p);

				// 后置拦截栈调用
				if (afterSet != null) {
					for (BeanHandle after : afterSet) {
						Object[] afterP = getParams(request, response, after);
						afterRet = after.invoke(afterP);
						if (afterRet != null) {
							lastAfter = after;
							break;
						}
					}
				}
			}

			// 视图渲染
			try {
				if (afterRet != null) {
					lastAfter.getViewHandle().render(request, response,
							afterRet);
				} else if (beforeRet != null) {
					lastBefore.getViewHandle().render(request, response,
							beforeRet);
				} else {
					beanHandle.getViewHandle().render(request, response, ret);
				}
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			scNotFound(request, response);
		}
	}

	public HttpServletDispatcherController init(String initParam) {
		webContext = new AnnotationWebContext(initParam);
		return this;
	}

	/**
	 * controller方法参数注入
	 * @param request
	 * @param response
	 * @param beanHandle
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Object[] getParams(HttpServletRequest request,
			HttpServletResponse response, BeanHandle beanHandle) {
		byte[] methodParam = beanHandle.getMethodParam();
		ParamHandle[] paramHandles = beanHandle.getParamHandles();
		Object[] p = new Object[methodParam.length];
		for (int i = 0; i < p.length; i++) {
			switch (methodParam[i]) {
			case MethodParam.REQUEST:
				p[i] = request;
				break;
			case MethodParam.RESPONSE:
				p[i] = response;
				break;
			case MethodParam.HTTP_PARAM:
				// 请求参数封装到javabean
				Enumeration<String> enumeration = request.getParameterNames();
				ParamHandle paramHandle = paramHandles[i];
				p[i] = paramHandle.newParamInstance();

				// 把http参数赋值给参数对象
				while (enumeration.hasMoreElements()) {
					String httpParamName = enumeration.nextElement();
					String paramValue = request.getParameter(httpParamName);
					paramHandle.setParam(p[i], httpParamName, paramValue);
				}
				if (VerifyUtils.isNotEmpty(paramHandle.getAttribute())) {
					request.setAttribute(paramHandle.getAttribute(), p[i]);
				}
				break;
			}
		}
		return p;
	}

	private void scNotFound(HttpServletRequest request,
			HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.setHeader("Content-Type", "text/html; charset="
				+ webContext.getEncoding());
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.print("<html><body>");
		writer.print("<h2>HTTP ERROR 404</h2>");
		writer.print("<hr/><i><small>firefly mvc framework</small></i>");
		writer.print("</body></html>");
		writer.close();
	}

}
