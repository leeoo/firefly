package com.firefly.annotation;

public @interface Interceptor {
	String value() default "";

	String uri();
}
