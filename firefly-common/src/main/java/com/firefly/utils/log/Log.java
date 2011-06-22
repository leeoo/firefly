package com.firefly.utils.log;

public interface Log {
	int TRACE = 0;
	int DEBUG = 1;
	int INFO = 2;
	int WARN = 3;
	int ERROR = 4;
	String CL = "\r\n";

	void trace(String str, Object... objs);

	void trace(String str, Throwable throwable, Object... objs);

	void debug(String str, Object... objs);

	void debug(String str, Throwable throwable, Object... objs);

	void info(String str, Object... objs);

	void info(String str, Throwable throwable, Object... objs);

	void warn(String str, Object... objs);

	void warn(String str, Throwable throwable, Object... objs);

	void error(String str, Object... objs);

	void error(String str, Throwable throwable, Object... objs);
}
