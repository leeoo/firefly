package com.firefly.mvc.web.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import com.firefly.annotation.HttpParam;

/**
 * 保存请求key对应的对象
 *
 * @author alvinqiu
 *
 */
public class BeanHandle implements Comparable<BeanHandle> {
	private final Object object;
	private final Method method;
	private final String[] paraClassNames;
	private final ParamHandle[] paramHandles;
	private final ViewHandle viewHandle;
	private Integer interceptOrder;

	public BeanHandle(Object object, Method method, ViewHandle viewHandle) {
		super();
		this.object = object;
		this.method = method;
		this.viewHandle = viewHandle;

		Class<?>[] paraTypes = method.getParameterTypes();
		paraClassNames = new String[paraTypes.length];
		paramHandles = new ParamHandle[paraTypes.length];
		Annotation[][] annotations = method.getParameterAnnotations();
		for (int i = 0; i < paraTypes.length; i++) {
			HttpParam httpParam = getHttpParam(annotations[i]);
			if (httpParam != null) {
				ParamHandle paramHandle = new ParamHandle();
				paramHandle.setAttribute(httpParam.value());
				paramHandle.setMap(getParamMap(paraTypes[i]));
				paramHandle.setParamClass(paraTypes[i]);
				paramHandles[i] = paramHandle;
				paraClassNames[i] = HttpParam.class.getName();
			} else {
				paraClassNames[i] = paraTypes[i].getName();
			}
		}
	}

	private Map<String, Method> getParamMap(Class<?> paraType) {
		Map<String, Method> paramMap = new HashMap<String, Method>();
		Method[] paramMethods = paraType.getMethods();

		for (Method paramMethod : paramMethods) {
			String paramName = null;
			if (paramMethod.getName().startsWith("set")) {
				paramName = String.valueOf(paramMethod.getName().charAt(3))
						.toLowerCase()
						+ paramMethod.getName().substring(4);
			}
			if (paramMethod.getName().startsWith("is")) {
				paramName = String.valueOf(paramMethod.getName().charAt(2))
						.toLowerCase()
						+ paramMethod.getName().substring(3);
			}
			if (paramName != null) {
				paramMethod.setAccessible(true);
				paramMap.put(paramName, paramMethod);
			}
		}
		return paramMap;
	}

	private HttpParam getHttpParam(Annotation[] annotations) {
		for (Annotation a : annotations) {
			if (a.annotationType().equals(HttpParam.class))
				return (HttpParam) a;
		}
		return null;
	}

	public ParamHandle[] getParamHandles() {
		return paramHandles;
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

	public String[] getParaClassNames() {
		return paraClassNames;
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
			// log.info("method isAccessible [{}]", method.isAccessible());
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

	@Override
	public int compareTo(BeanHandle o) {
		if (method.getName().equals("before"))
			return interceptOrder.compareTo(o.getInterceptOrder());
		else
			return o.getInterceptOrder().compareTo(interceptOrder);
	}

	// public static void main(String[] args) {
	// System.out.println(HttpParam.class.getName());
	// }
}
