package com.firefly.mvc.web.support;

public interface MethodParam {
	byte REQUEST = 0x00;
	byte RESPONSE = 0x01;
	byte HTTP_PARAM = 0x02;
	byte CONTROLLER_RETURN = 0x03;
}
