package com.firefly.server.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.firefly.net.Session;
import com.firefly.utils.time.SafeSimpleDateFormat;

public class HttpServletResponseImpl implements HttpServletResponse {

	public static SafeSimpleDateFormat GMT_FORMAT;
	boolean system = false;
	private boolean committed = false;
	private Session session;
	private HttpServletRequestImpl request;
	private int status, bufferSize;
	private String characterEncoding, shortMessage;
	private Map<String, String> headMap = new HashMap<String, String>();
	private List<Cookie> cookies = new LinkedList<Cookie>();

	static {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		GMT_FORMAT = new SafeSimpleDateFormat(sdf);
	}

	public HttpServletResponseImpl(Session session,
			HttpServletRequestImpl request, String characterEncoding) {
		this.session = session;
		this.request = request;
		this.characterEncoding = characterEncoding;
	}

	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	@Override
	public String getContentType() {
		return headMap.get("Content-Type");
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setCharacterEncoding(String charset) {
		characterEncoding = charset;
	}

	@Override
	public void setContentLength(int len) {
		headMap.put("Content-Length", String.valueOf(len));
	}

	@Override
	public void setContentType(String type) {
		headMap.put("Content-Type", type);
	}

	@Override
	public void setBufferSize(int size) {
		bufferSize = size;
	}

	@Override
	public int getBufferSize() {
		return bufferSize;
	}

	@Override
	public void flushBuffer() throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetBuffer() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCommitted() {
		return committed;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setLocale(Locale loc) {
		// TODO Auto-generated method stub

	}

	@Override
	public Locale getLocale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCookie(Cookie cookie) {
		cookies.add(cookie);
	}

	@Override
	public boolean containsHeader(String name) {
		return headMap.containsKey(name);
	}

	@Override
	public String encodeURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeRedirectURL(String url) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encodeUrl(String url) {
		return encodeURL(url);
	}

	@Override
	public String encodeRedirectUrl(String url) {
		return encodeRedirectURL(url);
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendError(int sc) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendRedirect(String location) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDateHeader(String name, long date) {
		setHeader(name, GMT_FORMAT.format(new Date(date)));
	}

	@Override
	public void addDateHeader(String name, long date) {
		addHeader(name, GMT_FORMAT.format(new Date(date)));
	}

	@Override
	public void setHeader(String name, String value) {
		headMap.put(name, value);
	}

	@Override
	public void addHeader(String name, String value) {
		String v = headMap.get(name);
		if (v != null) {
			v += "," + value;
			setHeader(name, v);
		} else
			setHeader(name, value);
	}

	@Override
	public void setIntHeader(String name, int value) {
		setHeader(name, String.valueOf(value));
	}

	@Override
	public void addIntHeader(String name, int value) {
		addHeader(name, String.valueOf(value));
	}

	@Override
	public void setStatus(int status) {
		this.status = status;
		this.shortMessage = Constants.STATUS_CODE.get(status);
	}

	@Override
	public void setStatus(int status, String shortMessage) {
		this.status = status;
		this.shortMessage = shortMessage;
	}

}
