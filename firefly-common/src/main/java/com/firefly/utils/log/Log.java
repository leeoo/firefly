package com.firefly.utils.log;

public interface Log {
	int TRACE = 0;
	int DEBUG = 1;
	int INFO = 2;
	int WARN = 3;
	int ERROR = 4;
	
	void trace(String str, Object... obj);

	void trace(String str, Throwable throwable, Object... obj);

	void debug(String str, String... obj);

	void debug(String str, Throwable throwable, Object... obj);
	
	void info(String str, String... obj);

	void info(String str, Throwable throwable, Object... obj);
	
	void warn(String str, String... obj);

	void warn(String str, Throwable throwable, Object... obj);
	
	void error(String str, String... obj);

	void error(String str, Throwable throwable, Object... obj);
}
