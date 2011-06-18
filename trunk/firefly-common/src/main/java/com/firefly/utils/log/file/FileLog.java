package com.firefly.utils.log.file;

import com.firefly.utils.log.Log;

import java.util.concurrent.BlockingQueue;

public class FileLog implements Log {
    private int level;
    private String path;
    private String name;

	@Override
	public void trace(String str, Object... obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void trace(String str, Throwable throwable, Object... obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String str, String... obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void debug(String str, Throwable throwable, Object... obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String str, String... obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void info(String str, Throwable throwable, Object... obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String str, String... obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void warn(String str, Throwable throwable, Object... obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String str, String... obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void error(String str, Throwable throwable, Object... obj) {
		// TODO Auto-generated method stub
		
	}



}
