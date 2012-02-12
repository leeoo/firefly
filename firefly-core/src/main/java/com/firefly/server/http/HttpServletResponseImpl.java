package com.firefly.server.http;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

import com.firefly.mvc.web.servlet.SystemHtmlPage;
import com.firefly.net.Session;
import com.firefly.server.exception.HttpServerException;
import com.firefly.server.io.ChunkedOutputStream;
import com.firefly.server.io.HttpServerOutpuStream;
import com.firefly.server.io.NetBufferedOutputStream;
import com.firefly.server.io.StaticFileOutputStream;
import com.firefly.utils.VerifyUtils;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;
import com.firefly.utils.time.SafeSimpleDateFormat;

public class HttpServletResponseImpl implements HttpServletResponse {

	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	public static SafeSimpleDateFormat GMT_FORMAT;
	private boolean committed;
	private HttpServletRequestImpl request;
	private int status, bufferSize;
	private String characterEncoding, shortMessage;
	private Map<String, String> headMap = new HashMap<String, String>();
	private List<Cookie> cookies = new LinkedList<Cookie>();
	private boolean usingWriter, usingOutputStream, usingFileOutputStream;
	private HttpServerOutpuStream out;
	private StaticFileOutputStream fileOut;
	private PrintWriter writer;
	private NetBufferedOutputStream bufferedOutput;

	boolean system;
	String systemResponseContent;

	static {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		GMT_FORMAT = new SafeSimpleDateFormat(sdf);
	}

	public HttpServletResponseImpl(Session session,
			HttpServletRequestImpl request, String characterEncoding,
			int bufferSize) {
		this.request = request;
		this.characterEncoding = characterEncoding;
		this.bufferSize = bufferSize;

		setStatus(200);
		setHeader("Server", "firefly-server/1.0");
	}

	private void createOutput() {
		if (bufferedOutput == null) {
			setHeader("Date", GMT_FORMAT.format(new Date()));
			setHeader("Connection", request.isKeepAlive() ? "keep-alive"
					: "close");
			bufferedOutput = new NetBufferedOutputStream(request.session,
					bufferSize, request.isKeepAlive());
			out = request.isChunked() ? new ChunkedOutputStream(bufferSize,
					bufferedOutput, this) : new HttpServerOutpuStream(
					bufferSize, bufferedOutput, this);
			fileOut = new StaticFileOutputStream(bufferSize, bufferedOutput,
					this);
			writer = new PrintWriter(out);
		}
	}

	public byte[] getHeadData() {
		StringBuilder sb = new StringBuilder();
		sb.append(request.getProtocol()).append(' ').append(status).append(' ')
				.append(shortMessage).append("\r\n");

		for (String name : headMap.keySet())
			sb.append(name).append(": ").append(headMap.get(name))
					.append("\r\n");

		// TODO 这里还需要Cookie处理

		sb.append("\r\n");
		return stringToByte(sb.toString());
	}

	public byte[] getChunkedSize(int length) {
		return stringToByte(Integer.toHexString(length) + "\r\n");
	}

	public byte[] stringToByte(String str) {
		byte[] ret = null;
		try {
			ret = str.getBytes(characterEncoding);
		} catch (UnsupportedEncodingException e) {
			log.error("string to bytes", e);
		}
		return ret;
	}

	@Override
	public String getCharacterEncoding() {
		return characterEncoding;
	}

	@Override
	public String getContentType() {
		return headMap.get("Content-Type");
	}

	public StaticFileOutputStream getStaticFileOutputStream()
			throws IOException {
		if (usingWriter)
			throw new HttpServerException(
					"getWriter has already been called for this response");
		if (usingOutputStream)
			throw new HttpServerException(
					"getOutputStream has already been called for this response");

		createOutput();
		usingFileOutputStream = true;
		return fileOut;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (usingWriter)
			throw new HttpServerException(
					"getWriter has already been called for this response");
		if (usingFileOutputStream)
			throw new HttpServerException(
					"getStaticFileOutputStream has already been called for this response");

		createOutput();

		usingOutputStream = true;
		return out;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (usingOutputStream)
			throw new HttpServerException(
					"getOutputStream has already been called for this response");
		if (usingFileOutputStream)
			throw new HttpServerException(
					"getStaticFileOutputStream has already been called for this response");

		createOutput();

		usingWriter = true;
		return writer;
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
		out.flush();
	}

	@Override
	public void resetBuffer() {
		out.resetBuffer();
	}

	@Override
	public boolean isCommitted() {
		return committed;
	}

	public void setCommitted(boolean committed) {
		this.committed = committed;
	}

	@Override
	public void reset() {
		usingWriter = false;
		usingOutputStream = false;
		committed = false;
		bufferedOutput = null;
		out = null;
		writer = null;
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
		throw new HttpServerException("no implements this method!");
	}

	@Override
	public String encodeRedirectURL(String url) {
		throw new HttpServerException("no implements this method!");
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
		setStatus(sc, msg);
		systemResponseContent = shortMessage;
		outSystemData();
	}

	@Override
	public void sendError(int sc) throws IOException {
		setStatus(sc);
		systemResponseContent = shortMessage;
		outSystemData();
	}

	public void outSystemData() throws IOException {
		if (isCommitted())
			throw new IllegalStateException("response is committed");

		createOutput();
		try {
			boolean hasContent = VerifyUtils.isNotEmpty(systemResponseContent);
			byte[] b = null;
			if (status != 100) {
				if (hasContent) {
					b = SystemHtmlPage.systemPageTemplate(status,
							systemResponseContent).getBytes(characterEncoding);
					setHeader("Content-Length", String.valueOf(b.length));
				} else {
					setHeader("Content-Length", "0");
				}
			}

			bufferedOutput.write(getHeadData());
			if (hasContent)
				bufferedOutput.write(b);
		} finally {
			bufferedOutput.close();
		}

		setCommitted(true);
	}

	public void scheduleSendContinue(int sc) {
		setStatus(sc);
		system = true;
	}

	public void scheduleSendError(int sc, String content) {
		setStatus(sc);
		systemResponseContent = content;
		system = true;
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		String absolute = toAbsolute(location);
		setStatus(SC_FOUND);
		setHeader("Location", absolute);
		setHeader("Content-Length", "0");
		outHeadData();
	}

	public void outHeadData() throws IOException {
		if (isCommitted())
			throw new IllegalStateException("response is committed");

		createOutput();
		try {
			bufferedOutput.write(getHeadData());
		} finally {
			bufferedOutput.close();
		}
		setCommitted(true);
	}

	public String toAbsolute(String location) {
		if (location.startsWith("http"))
			return location;

		StringBuilder sb = new StringBuilder();
		sb.append(request.getScheme()).append("://")
				.append(request.getServerName()).append(":")
				.append(request.getServerPort());

		if (location.charAt(0) == '/') {
			sb.append(location);
		} else {
			String URI = request.getRequestURI();
			int last = 0;
			for (int i = URI.length() - 1; i >= 0; i--) {
				if (URI.charAt(i) == '/') {
					last = i + 1;
					break;
				}
			}
			sb.append(URI.substring(0, last)).append(location);
		}
		return sb.toString();
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
