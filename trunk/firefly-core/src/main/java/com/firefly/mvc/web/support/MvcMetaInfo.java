package com.firefly.mvc.web.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.firefly.annotation.HttpParam;

/**
 * 保存请求key对应的对象
 *
 * @author alvinqiu
 *
 */
public class MvcMetaInfo implements Comparable<MvcMetaInfo> {
	private static Logger log = LoggerFactory.getLogger(MvcMetaInfo.class);
	private final Object object; // controller的实例对象
	private final Method method; // 请求uri对应的方法
	private final ParamMetaInfo[] paramMetaInfos; // @HttpParam标注的类的元信息
	private final byte[] methodParam; // 请求方法参数类型
	private final ViewHandle viewHandle; // 返回的视图
	private Integer interceptOrder; // 拦截链顺序

	public MvcMetaInfo(Object object, Method method, ViewHandle viewHandle) {
		super();
		this.object = object;
		this.method = method;
		this.viewHandle = viewHandle;

		Class<?>[] paraTypes = method.getParameterTypes();
		methodParam = new byte[paraTypes.length];
		// 构造参数对象
		paramMetaInfos = new ParamMetaInfo[paraTypes.length];
		Annotation[][] annotations = method.getParameterAnnotations();
		for (int i = 0; i < paraTypes.length; i++) {
			HttpParam httpParam = getHttpParam(annotations[i]);
			if (httpParam != null) {
				ParamMetaInfo paramMetaInfo = new ParamMetaInfo(paraTypes[i],
						getBeanSetMethod(paraTypes[i]), httpParam.value());
				paramMetaInfos[i] = paramMetaInfo;
				methodParam[i] = MethodParam.HTTP_PARAM;
			} else {
				if (paraTypes[i].equals(HttpServletRequest.class))
					methodParam[i] = MethodParam.REQUEST;
				else if (paraTypes[i].equals(HttpServletResponse.class))
					methodParam[i] = MethodParam.RESPONSE;
			}
		}
	}

	/**
	 * 根据类型获取所有set方法
	 * @param paraType
	 * @return
	 */
	private Map<String, Method> getBeanSetMethod(Class<?> paraType) {
		Map<String, Method> beanSetMethod = new HashMap<String, Method>();
		Method[] paramMethods = paraType.getMethods();

		for (Method paramMethod : paramMethods) {

			if (!paramMethod.getName().startsWith("set")
					|| Modifier.isStatic(paramMethod.getModifiers())
					|| !paramMethod.getReturnType().equals(Void.TYPE)
					|| paramMethod.getParameterTypes().length != 1) {
				continue;
			}
			// log.debug("paramMethod [{}]", paramMethod.getName());
			// 根据javabean里面的set方法取出对应的属性
			String paramName = Character.toLowerCase(paramMethod.getName()
					.charAt(3))
					+ paramMethod.getName().substring(4);
			paramMethod.setAccessible(true);
			beanSetMethod.put(paramName, paramMethod);
		}
		log.debug(beanSetMethod.toString());
		return beanSetMethod;
	}

	private HttpParam getHttpParam(Annotation[] annotations) {
		for (Annotation a : annotations) {
			if (a.annotationType().equals(HttpParam.class))
				return (HttpParam) a;
		}
		return null;
	}

	public ParamMetaInfo[] getParamMetaInfos() {
		return paramMetaInfos;
	}

	public Integer getInterceptOrder() {
		return interceptOrder;
	}

	public void setInterceptOrder(Integer interceptOrder) {
		this.interceptOrder = interceptOrder;
	}

	public ViewHandle getViewHandle() {
		return viewHandle;
	}

	public Object getObject() {
		return object;
	}

	public Method getMethod() {
		return method;
	}

	public Object invoke(Object[] args) {
		Object ret = null;
		try {
			ret = method.invoke(object, args);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public byte[] getMethodParam() {
		return methodParam;
	}

	@Override
	public int compareTo(MvcMetaInfo o) {
		if (method.getName().equals("before"))
			return interceptOrder.compareTo(o.getInterceptOrder());
		else
			return o.getInterceptOrder().compareTo(interceptOrder);
	}
}
