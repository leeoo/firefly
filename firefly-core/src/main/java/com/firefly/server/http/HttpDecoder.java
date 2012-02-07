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
	public static final String REMAIN_DATA = "remain_data";
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
		ByteBuffer prev = (ByteBuffer) session.getAttribute(REMAIN_DATA);

		if (prev != null) {
			session.removeAttribute(REMAIN_DATA);
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

	abstract private class AbstractHttpDecoder {
		private void decode0(ByteBuffer now, Session session,
				HttpServletRequestImpl req) throws Throwable {
			if (decode(now, session, req))
				next(now.slice(), session, req);
			else
				save(now, session);
		}

		private void save(ByteBuffer buf, Session session) {
			if (buf.hasRemaining())
				session.setAttribute(REMAIN_DATA, buf);
		}

		private void next(ByteBuffer buf, Session session,
				HttpServletRequestImpl req) throws Throwable {
			req.status++;
			if (req.status < httpDecode.length) {
				req.offset = 0;
				httpDecode[req.status].decode(buf, session, req);
			}
		}

		protected void clear(Session session) {
			session.removeAttribute(REMAIN_DATA);
			session.removeAttribute(HTTP_REQUEST);
		}

		protected void response(Session session, HttpServletRequestImpl req,
				int httpStatus) {
			try {
				clear(session);
				req.status = httpDecode.length;
				// TODO response msg

			} finally {
				session.close(false);
			}
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
				if (req.offset >= config.getMaxRequestLineLength()) {
					log.error("request line length is {}, it more than {}",
							len, config.getMaxRequestLineLength());
					response(session, req, 414);
					return false;
				}

				if (buf.get(req.offset) == LINE_LIMITOR) {
					byte[] data = new byte[req.offset + 1];
					buf.get(data);
					String requestLine = new String(data, config.getEncoding())
							.trim();
					if (VerifyUtils.isEmpty(requestLine)) {
						log.error("request line length is 0");
						response(session, req, 400);
						return false;
					}

					String[] reqLine = StringUtils.split(requestLine, ' ');
					if (reqLine.length > 3) {
						log.error("request line format error: {}", requestLine);
						response(session, req, 400);
						return false;
					}

					int s = reqLine[1].indexOf('?');
					req.method = reqLine[0].toUpperCase();
					if (s > 0) {
						req.requestURI = reqLine[1].substring(0, s);
						req.queryString = reqLine[1].substring(s + 1,
								reqLine[1].length());
					} else
						req.requestURI = reqLine[1];
					req.protocol = reqLine[2];
					return true;
				}
			}
			return false;
		}

	}

	private class HeadDecoder extends AbstractHttpDecoder {

		@Override
		public boolean decode(ByteBuffer buf, Session session,
				HttpServletRequestImpl req) throws Throwable {
			// TODO Auto-generated method stub
			int len = req.offset + buf.remaining();
			System.out.println(len);
			for (; req.offset < len; req.offset++) {

			}
			System.out.println(req.offset);
			byte[] data = new byte[len];
			buf.get(data);
			System.out.println(new String(data, config.getEncoding()));
			return false;
		}

	}

	private class BodyDecoder extends AbstractHttpDecoder {

		@Override
		public boolean decode(ByteBuffer buf, Session session,
				HttpServletRequestImpl req) throws Throwable {
			// TODO 调用handler前要clear
			return false;
		}

	}

}
