package com.firefly.server.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.firefly.server.session.HttpSessionManager;

public class Config {
	private String encoding = "UTF-8";
	private int maxRequestLineLength = 8 * 1024,
			maxRequestHeadLength = 16 * 1024, maxRangeNum = 8,
			writeBufferSize = 8 * 1024, handlerSize;
	private long maxUploadLength = 50 * 1024 * 1024;
	private boolean keepAlive = true;
	private String serverHome, host, servletPath = "/app", contextPath = "",
			sessionIdName = "jsessionid";
	private int port;
	private HttpSessionManager httpSessionManager;
	private FileAccessFilter fileAccessFilter = new FileAccessFilter() {
		@Override
		public String doFilter(HttpServletRequest request,
				HttpServletResponse response) {
			return request.getRequestURI();
		}
	};

	{
		int p = Runtime.getRuntime().availableProcessors();
		if (p > 4)
			handlerSize = p * 2;
		else
			handlerSize = p + 1;
	}

	public Config() {
	}

	public Config(String serverHome, String host, int port) {
		setServerHome(serverHome);
		this.host = host;
		this.port = port;
	}

	public String getSessionIdName() {
		return sessionIdName;
	}

	public void setSessionIdName(String sessionIdName) {
		this.sessionIdName = sessionIdName;
	}

	public HttpSessionManager getHttpSessionManager() {
		return httpSessionManager;
	}

	public void setHttpSessionManager(HttpSessionManager httpSessionManager) {
		this.httpSessionManager = httpSessionManager;
	}

	public FileAccessFilter getFileAccessFilter() {
		return fileAccessFilter;
	}

	public void setFileAccessFilter(FileAccessFilter fileAccessFilter) {
		this.fileAccessFilter = fileAccessFilter;
	}

	public int getHandlerSize() {
		return handlerSize;
	}

	public void setHandlerSize(int handlerQueueSize) {
		this.handlerSize = handlerQueueSize;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = removeLastSlash(contextPath);
	}

	public String getServletPath() {
		return servletPath;
	}

	public void setServletPath(String servletPath) {
		this.servletPath = removeLastSlash(servletPath);
	}

	public static String removeLastSlash(String str) {
		if (str.charAt(str.length() - 1) == '/')
			return str.substring(0, str.length() - 1);
		return str;
	}

	public int getWriteBufferSize() {
		return writeBufferSize;
	}

	public void setWriteBufferSize(int writeBufferSize) {
		this.writeBufferSize = writeBufferSize;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getServerHome() {
		return serverHome;
	}

	public void setServerHome(String serverHome) {
		if (serverHome.charAt(serverHome.length() - 1) == '/')
			this.serverHome = serverHome.substring(0, serverHome.length() - 1);
		else
			this.serverHome = serverHome;
	}

	public int getMaxRequestHeadLength() {
		return maxRequestHeadLength;
	}

	public void setMaxRequestHeadLength(int maxRequestHeadLength) {
		this.maxRequestHeadLength = maxRequestHeadLength;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public int getMaxRequestLineLength() {
		return maxRequestLineLength;
	}

	public void setMaxRequestLineLength(int maxRequestLineLength) {
		this.maxRequestLineLength = maxRequestLineLength;
	}

	public int getMaxRangeNum() {
		return maxRangeNum;
	}

	public void setMaxRangeNum(int maxRangeNum) {
		this.maxRangeNum = maxRangeNum;
	}

	public long getMaxUploadLength() {
		return maxUploadLength;
	}

	public void setMaxUploadLength(long maxUploadLength) {
		this.maxUploadLength = maxUploadLength;
	}

	public boolean isKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}

}
