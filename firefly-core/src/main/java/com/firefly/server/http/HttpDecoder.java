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
			req.response = new HttpServletResponseImpl();
			req.response.characterEncoding = config.getEncoding();
			req.response.session = session;
			session.setAttribute(HTTP_REQUEST, req);
		}
		return req;
	}

	abstract private class AbstractHttpDecoder {
		private void decode0(ByteBuffer now, Session session,
				HttpServletRequestImpl req) throws Throwable {
			boolean next = decode(now, session, req);
			if (next)
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
				httpDecode[req.status].decode0(buf, session, req);
			}
		}

		private void finish(Session session, HttpServletRequestImpl req) {
			session.removeAttribute(REMAIN_DATA);
			req.status = httpDecode.length;
		}

		protected void responseError(Session session,
				HttpServletRequestImpl req, int httpStatus) {
			finish(session, req);
			req.response.system = true;
			req.response.setStatus(httpStatus);
			session.fireReceiveMessage(req);
		}

		protected void response(Session session, HttpServletRequestImpl req) {
			finish(session, req);
			session.fireReceiveMessage(req);
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
					log.error("request line length is {}, it more than {}|{}",
							req.offset, config.getMaxRequestLineLength(),
							session.getRemoteAddress().toString());
					responseError(session, req, 414);
					return true;
				}

				if (buf.get(req.offset) == LINE_LIMITOR) {
					byte[] data = new byte[req.offset + 1];
					buf.get(data);
					String requestLine = new String(data, config.getEncoding())
							.trim();
					if (VerifyUtils.isEmpty(requestLine)) {
						log.error("request line length is 0|{}", session
								.getRemoteAddress().toString());
						responseError(session, req, 400);
						return true;
					}

					String[] reqLine = StringUtils.split(requestLine, ' ');
					if (reqLine.length > 3) {
						log.error("request line format error: {}|{}",
								requestLine, session.getRemoteAddress()
										.toString());
						responseError(session, req, 400);
						return true;
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
			int len = buf.remaining();

			for (int i = req.offset, p = 0; i < len; i++) {
				if (buf.get(i) == LINE_LIMITOR) {
					int parseLen = i - p + 1;
					req.headLength += parseLen;

					if (req.headLength >= config.getMaxRequestHeadLength()) {
						log.error(
								"request head length is {}, it more than {}|{}",
								req.offset, config.getMaxRequestHeadLength(),
								session.getRemoteAddress().toString());
						responseError(session, req, 400);
						return true;
					}

					byte[] data = new byte[parseLen];
					buf.get(data);
					String headLine = new String(data, config.getEncoding())
							.trim();
					p = i + 1;

					if (VerifyUtils.isEmpty(headLine)) {
						if (!req.getMethod().equals("POST")
								&& !req.getMethod().equals("PUT"))
							response(session, req);
						return true;
					} else {
						int h = headLine.indexOf(':');
						if (h <= 0) {
							log.error("head line format error: {}|{}",
									headLine, session.getRemoteAddress()
											.toString());
							responseError(session, req, 400);
							return true;
						}

						String name = headLine.substring(0, h).toLowerCase()
								.trim();
						String value = headLine.substring(h + 1).trim();
						req.headMap.put(name, value);
						req.offset = len - i - 1;

						if (name.equals("expect") && value.startsWith("100-")
								&& req.getProtocol().equals("HTTP/1.1"))
							response100Continue(session, req);
					}
				}
			}
			return false;
		}

		private void response100Continue(Session session,
				HttpServletRequestImpl req) {
			req.response.setStatus(100);
			req.response.system = true;
			session.fireReceiveMessage(req);
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
