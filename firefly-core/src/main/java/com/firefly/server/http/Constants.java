package com.firefly.server.http;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	public static final Map<Integer, String> STATUS_CODE = new HashMap<Integer, String>();
	
	static {
		// 1xx
		STATUS_CODE.put(100, "Continue");
		STATUS_CODE.put(101, "Switching Protocols");
		
		// 2xx
		STATUS_CODE.put(200, "OK");
		STATUS_CODE.put(201, "Created");
		STATUS_CODE.put(202, "Accepted");
		STATUS_CODE.put(203, "Non-Authoritative information");
		STATUS_CODE.put(204, "No Content");
		STATUS_CODE.put(205, "Reset Content");
		STATUS_CODE.put(206, "Partial Content");
		
		// 3xx
		STATUS_CODE.put(300, "Multiple Choices");
		STATUS_CODE.put(301, "Moved Permanently");
		STATUS_CODE.put(302, "Found");
		STATUS_CODE.put(303, "See Other");
		STATUS_CODE.put(304, "Not Modified");
		STATUS_CODE.put(305, "User Proxy");
		STATUS_CODE.put(307, "Temporary Redirect");
		
		// 4xx
		STATUS_CODE.put(400, "Bad Request");
		STATUS_CODE.put(401, "Unauthorized");
		STATUS_CODE.put(403, "Forbidden");
		STATUS_CODE.put(404, "Not Found");
		STATUS_CODE.put(405, "Method Not Allowed");
		STATUS_CODE.put(406, "Not Acceptable");
		STATUS_CODE.put(407, "Proxy Authentication Required");
		STATUS_CODE.put(408, "Request Timeout");
		STATUS_CODE.put(409, "Confilict");
		STATUS_CODE.put(410, "Gone");
		STATUS_CODE.put(411, "Length Required");
		STATUS_CODE.put(412, "Precondition Failed");
		STATUS_CODE.put(413, "Request Entity Too Large");
		STATUS_CODE.put(414, "Request-URI Too Long");
		STATUS_CODE.put(415, "Unsupported Media Type");
		STATUS_CODE.put(416, "Requested Range Not Satisfiable");
		STATUS_CODE.put(417, "Expectation Failed");
		
		// 5xx
		STATUS_CODE.put(500, "Internal Server Error");
		STATUS_CODE.put(501, "Not Implemented");
		STATUS_CODE.put(503, "Service Unavailable");
		STATUS_CODE.put(504, "Gateway Timeout");
		STATUS_CODE.put(505, "HTTP Version Not Supported");
	}
}
