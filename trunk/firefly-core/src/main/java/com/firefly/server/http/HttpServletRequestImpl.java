package com.firefly.server.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.firefly.net.Session;
import com.firefly.utils.StringUtils;
import com.firefly.utils.VerifyUtils;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class HttpServletRequestImpl implements HttpServletRequest {
	int status, headLength, offset;
	String method, requestURI, queryString, protocol;

	PipedInputStream pipedInputStream = new PipedInputStream();
	PipedOutputStream pipedOutputStream;
	Cookie[] cookies;
	Map<String, String> headMap = new HashMap<String, String>();
	HttpServletResponseImpl response;

	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	private static final String[] EMPTY_STR_ARR = new String[0];
	private String characterEncoding;
	private Session session;
	private Map<String, List<String>> parameterMap = new HashMap<String, List<String>>();
	private Map<String, Object> attributeMap = new HashMap<String, Object>();
	private boolean loadParam = false;
	private BufferedReader bufferedReader;
	private ServletInputStream servletInputStream = new ServletInputStream() {

		@Override
		public int read() throws IOException {
			return pipedInputStream.read();
		}

		@Override
		public int available() throws IOException {
			return pipedInputStream.available();
		}

		@Override
		public void close() throws IOException {
			pipedInputStream.close();
		}

		public int read(byte[] b, int off, int len) throws IOException {
			return pipedInputStream.read(b, off, len);
		}
	};

	public HttpServletRequestImpl(Session session, String characterEncoding) {
		this.characterEncoding = characterEncoding;
		this.session = session;
		response = new HttpServletResponseImpl(session, this, characterEncoding);
	}

	private void loadParam() {
		if (!loadParam) {
			try {
				loadParam(queryString);
				if (method.equals("POST")
						&& "application/x-www-form-urlencoded"
								.equals(getContentType())) {
					byte[] data = new byte[getContentLength()];

					ServletInputStream input = getInputStream();
					try {
						input.read(data);
						loadParam(new String(data, characterEncoding));
					} finally {
						input.close();
					}
				}
			} catch (Throwable t) {
				log.error("load param error", t);
			}
			loadParam = true;
		}
	}

	private void loadParam(String str) throws UnsupportedEncodingException {
		if (VerifyUtils.isNotEmpty(str)) {
			String[] p = StringUtils.split(str, '&');
			for (String kv : p) {
				int i = kv.indexOf('=');
				if (i > 0) {
					String name = kv.substring(0, i);
					String value = kv.substring(i + 1);

					List<String> list = parameterMap.get(name);
					if (list == null) {
						list = new ArrayList<String>();
						parameterMap.put(name, list);

					}
					list.add(URLDecoder.decode(value, characterEncoding));
				}

			}
		}
	}

	@Override
	public Object getAttribute(String name) {
		return attributeMap.get(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return new Enumeration<String>() {
			private Iterator<String> iterator = attributeMap.keySet()
					.iterator();

			@Override
			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			@Override
			public String nextElement() {
				return iterator.next();
			}
		};
	}

	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	@Override
	public void setCharacterEncoding(String characterEncoding)
			throws UnsupportedEncodingException {
		this.characterEncoding = characterEncoding;
	}

	@Override
	public int getContentLength() {
		return getIntHeader("Content-Length");
	}

	@Override
	public String getContentType() {
		return getHeader("Content-Type");
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return servletInputStream;
	}

	@Override
	public String getParameter(String name) {
		loadParam();
		List<String> list = parameterMap.get(name);
		return list != null ? list.get(0) : null;
	}

	@Override
	public Enumeration<String> getParameterNames() {
		loadParam();
		return new Enumeration<String>() {
			private Iterator<String> iterator = parameterMap.keySet()
					.iterator();

			@Override
			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			@Override
			public String nextElement() {
				return iterator.next();
			}
		};
	}

	@Override
	public String[] getParameterValues(String name) {
		loadParam();
		return parameterMap.get(name).toArray(EMPTY_STR_ARR);
	}

	@Override
	public Map<String, List<String>> getParameterMap() {
		loadParam();
		return parameterMap;
	}

	@Override
	public String getProtocol() {
		return protocol;
	}

	@Override
	public String getScheme() {
		return "http";
	}

	/**
	 * @return 服务器绑定的ip或者域名
	 */
	@Override
	public String getServerName() {
		return session.getLocalAddress().getHostName();
	}

	/**
	 * @return 服务器监听的端口
	 */
	@Override
	public int getServerPort() {
		return session.getLocalAddress().getPort();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		if (bufferedReader == null)
			bufferedReader = new BufferedReader(new InputStreamReader(
					pipedInputStream, characterEncoding));
		return bufferedReader;
	}

	@Override
	public String getRemoteAddr() {
		return session.getRemoteAddress().toString();
	}

	@Override
	public String getRemoteHost() {
		return session.getRemoteAddress().getHostName();
	}

	@Override
	public void setAttribute(String name, Object o) {
		attributeMap.put(name, o);
	}

	@Override
	public void removeAttribute(String name) {
		attributeMap.remove(name);
	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Enumeration<?> getLocales() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSecure() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRealPath(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRemotePort() {
		return session.getRemoteAddress().getPort();
	}

	/**
	 * @return 接收请求的ip或域名
	 */
	@Override
	public String getLocalName() {
		return session.getLocalAddress().getHostName();
	}

	/**
	 * @return 接收请求的ip
	 */
	@Override
	public String getLocalAddr() {
		return session.getLocalAddress().toString();
	}

	/**
	 * @return 接收请求的端口
	 */
	@Override
	public int getLocalPort() {
		return session.getLocalAddress().getPort();
	}

	@Override
	public String getAuthType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Cookie[] getCookies() {
		return cookies;
	}

	@Override
	public long getDateHeader(String name) {
		String v = getHeader(name);
		return v != null ? Long.parseLong(v) : 0;
	}

	@Override
	public String getHeader(String name) {
		return headMap.get(name.toLowerCase());
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		String value = getHeader(name);
		final String[] values = StringUtils.split(value, ',');
		return new Enumeration<String>() {
			private int i = 0;

			@Override
			public boolean hasMoreElements() {
				return i < values.length;
			}

			@Override
			public String nextElement() {
				return values[i++];
			}
		};
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return new Enumeration<String>() {
			private Iterator<String> iterator = headMap.keySet().iterator();

			@Override
			public boolean hasMoreElements() {
				return iterator.hasNext();
			}

			@Override
			public String nextElement() {
				return iterator.next();
			}
		};
	}

	@Override
	public int getIntHeader(String name) {
		String v = getHeader(name);
		return v != null ? Integer.parseInt(v) : 0;
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getPathInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPathTranslated() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getContextPath() {
		return "";
	}

	@Override
	public String getQueryString() {
		return queryString;
	}

	@Override
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUserInRole(String role) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Principal getUserPrincipal() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRequestURI() {
		return requestURI;
	}

	@Override
	public StringBuffer getRequestURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServletPath() {
		return "";
	}

	@Override
	public HttpSession getSession(boolean create) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HttpSession getSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return isRequestedSessionIdFromURL();
	}

	@Override
	public String toString() {
		return headMap.toString();
	}
}
