package com.firefly.server.http;

import java.nio.ByteBuffer;

import com.firefly.net.Decoder;
import com.firefly.net.Session;
import com.firefly.utils.StringUtils;
import com.firefly.utils.VerifyUtils;
import com.firefly.utils.log.Log;
import com.firefly.utils.log.LogFactory;

public class HttpDecoder implements Decoder {
	private static Log log = LogFactory.getInstance().getLog("firefly-system");
	private Config config;
	private AbstractHttpDecoder[] httpDecode = new AbstractHttpDecoder[] {
			new RequestLineDecoder(), new HeadDecoder(), new BodyDecoder() };
	public static final String HTTP_REQUEST = "http_req";
	private static final byte LINE_LIMITOR = '\n';

	public HttpDecoder(Config config) {
		this.config = config;
	}

	@Override
	public void decode(ByteBuffer buf, Session session) throws Throwable {
		ByteBuffer now = getBuffer(buf, session);
		HttpServletRequestImpl req = getHttpServletRequestImpl(session);
		httpDecode[req.status].decode0(now, session, req);
	}

	private ByteBuffer getBuffer(ByteBuffer buf, Session session) {
		ByteBuffer now = buf;
		ByteBuffer prev = (ByteBuffer) session.getAttribute("buff");

		if (prev != null) {
			session.removeAttribute("buff");
			now = (ByteBuffer) ByteBuffer
					.allocate(prev.remaining() + buf.remaining()).put(prev)
					.put(buf).flip();
		}
		return now;
	}

	private HttpServletRequestImpl getHttpServletRequestImpl(Session session) {
		HttpServletRequestImpl req = (HttpServletRequestImpl) session
				.getAttribute(HTTP_REQUEST);
		if (req == null) {
			req = new HttpServletRequestImpl();
			session.setAttribute(HTTP_REQUEST, req);
		}
		return req;
	}

	private void clear(Session session) {
		session.removeAttribute("buff");
		session.removeAttribute(HTTP_REQUEST);
	}

	abstract private class AbstractHttpDecoder {
		private void decode0(ByteBuffer now, Session session,
				HttpServletRequestImpl req) throws Throwable {
			boolean finished = decode(now, session, req);
			if (finished)
				next(now, session, req);
			else
				save(now, session);
		}

		private void save(ByteBuffer buf, Session session) {
			if (buf.hasRemaining())
				session.setAttribute("buff", buf);
		}

		private void next(ByteBuffer buf, Session session,
				HttpServletRequestImpl req) throws Throwable {
			req.status++;
			if (req.status < httpDecode.length)
				httpDecode[req.status].decode(buf, session, req);
		}

		abstract protected boolean decode(ByteBuffer buf, Session session,
				HttpServletRequestImpl req) throws Throwable;
	}

	private class RequestLineDecoder extends AbstractHttpDecoder {

		@Override
		public boolean decode(ByteBuffer buf, Session session,
				HttpServletRequestImpl req) throws Throwable {
			int len = buf.remaining();
			for (; req.offset < len; req.offset++) {
				if (buf.get(req.offset) == LINE_LIMITOR) {
					int requestLineLength = req.offset + 1;
					if (requestLineLength > config.getMaxRequestLineLength()) {
						log.error("request line length is {}, it more than {}",
								len, config.getMaxRequestLineLength());
						clear(session);
						// TODO response 414 Request-URI Too Long
						session.close(false);
						req.status = httpDecode.length;
						return false;
					}

					byte[] data = new byte[requestLineLength];
					buf.get(data);
					String requestLine = new String(data, config.getEncoding())
							.trim();
					if (VerifyUtils.isEmpty(requestLine)) {
						log.error("request line length is 0");
						clear(session);
						// TODO response 400 Bad Request
						session.close(false);
						req.status = httpDecode.length;
						return false;
					}

					String[] reqLine = StringUtils.split(requestLine, ' ');
					if (reqLine.length > 3) {
						log.error("request line format error: {}", requestLine);
						clear(session);
						// TODO response 400 Bad Request
						session.close(false);
						req.status = httpDecode.length;
						return false;
					}

					req.method = reqLine[0];
					req.requestURI = reqLine[1];
					req.protocol = reqLine[2];
					req.offset = requestLineLength;
					return true;
				}
			}
			req.offset = len;
			return false;
		}

	}

	private class HeadDecoder extends AbstractHttpDecoder {

		@Override
		public boolean decode(ByteBuffer buf, Session session,
				HttpServletRequestImpl req) throws Throwable {
			// TODO Auto-generated method stub
			int len = buf.remaining();
			for (; req.offset < len; req.offset++) {

			}
			return false;
		}

	}

	private class BodyDecoder extends AbstractHttpDecoder {

		@Override
		public boolean decode(ByteBuffer buf, Session session,
				HttpServletRequestImpl req) throws Throwable {
			// TODO 调用handler前要clear
			int len = buf.remaining();
			for (; req.offset < len; req.offset++) {

			}
			return false;
		}

	}

}
