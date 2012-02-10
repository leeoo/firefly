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
import java.util.Queue;
import java.util.TimeZone;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import com.firefly.net.Session;
import com.firefly.server.io.NetBufferedOutputStream;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;
import com.firefly.utils.time.SafeSimpleDateFormat;

public class HttpServletResponseImpl implements HttpServletResponse {

	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	public static SafeSimpleDateFormat GMT_FORMAT;
	boolean system = false;
	private boolean committed = false;
	private Session session;
	private HttpServletRequestImpl request;
	private int status, bufferSize;
	private String characterEncoding, shortMessage;
	private Map<String, String> headMap = new HashMap<String, String>();
	private List<Cookie> cookies = new LinkedList<Cookie>();
	private boolean usingWriter, usingOutputStream;
	private ChunkedOutputStream out;
	private PrintWriter writer;

	static {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss z", Locale.ENGLISH);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		GMT_FORMAT = new SafeSimpleDateFormat(sdf);
	}

	public HttpServletResponseImpl(Session session,
			HttpServletRequestImpl request, String characterEncoding,
			int bufferSize) {
		this.session = session;
		this.request = request;
		this.characterEncoding = characterEncoding;
		this.bufferSize = bufferSize;
		setStatus(200);
	}

	class ChunkedOutputStream extends ServletOutputStream {

		private NetBufferedOutputStream bufferedOutput;
		private Queue<ChunkedData> queue = new LinkedList<ChunkedData>();
		private int size;
		private boolean keepAlive;
		private byte[] crlf, endFlag;

		public ChunkedOutputStream(boolean keepAlive) {
			this.keepAlive = keepAlive;
			bufferedOutput = new NetBufferedOutputStream(session, bufferSize,
					request.isKeepAlive());
			crlf = stringToByte("\r\n");
			endFlag = stringToByte("0\r\n\r\n");
		}

		@Override
		public void write(int b) throws IOException {
			queue.offer(new ByteChunkedData((byte) b));
			size++;
			if (size > bufferSize)
				flush();
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			if (len > bufferSize) {
				flush();
				bufferedOutput.write(b, off, len);
				return;
			}

			queue.offer(new ByteArrayChunkedData(b, off, len));
			size += len;
			if (size > bufferSize)
				flush();
		}

		@Override
		public void flush() throws IOException {
			if (!committed) {
				setHeader("Connection", keepAlive ? "keep-alive" : "close");
				setHeader("Transfer-Encoding", "chunked");
				bufferedOutput.write(getHeadData());
				committed = true;
			}

			if (size > 0) {
				bufferedOutput.write(getChunkedSize(size));
				for (ChunkedData d = null; (d = queue.poll()) != null;) {
					d.write();
				}
				bufferedOutput.write(crlf);
				size = 0;
			}
		}

		@Override
		public void close() throws IOException {
			flush();
			bufferedOutput.write(endFlag);
			bufferedOutput.close();
		}

		public void resetBuffer() {
			bufferedOutput.resetBuffer();
			size = 0;
			queue.clear();
		}

		private abstract class ChunkedData {
			abstract void write() throws IOException;
		}

		private class ByteChunkedData extends ChunkedData {
			private byte b;

			public ByteChunkedData(byte b) {
				this.b = b;
			}

			@Override
			public void write() throws IOException {
				bufferedOutput.write(b);
			}
		}

		private class ByteArrayChunkedData extends ChunkedData {
			private byte[] b;
			private int off, len;

			public ByteArrayChunkedData(byte[] b, int off, int len) {
				this.b = b;
				this.off = off;
				this.len = len;
			}

			@Override
			public void write() throws IOException {
				bufferedOutput.write(b, off, len);
			}
		}

	}

	private byte[] getHeadData() {
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

	private byte[] getChunkedSize(int length) {
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

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (usingWriter)
			return null;

		if (out == null)
			out = new ChunkedOutputStream(request.isKeepAlive());
		usingOutputStream = true;
		return out;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (usingOutputStream)
			return null;

		if (out == null)
			out = new ChunkedOutputStream(request.isKeepAlive());
		if (writer == null)
			writer = new PrintWriter(out);
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
		setStatus(sc, msg);

	}

	@Override
	public void sendError(int sc) throws IOException {
		// TODO Auto-generated method stub
		setStatus(sc);

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
