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
		httpDecode[0].decode(buf, session);
	}

	abstract private class AbstractHttpDecoder implements Decoder {
		@Override
		public void decode(ByteBuffer buf, Session session) throws Throwable {
			ByteBuffer now = getBuffer(buf, session);
			HttpServletRequestImpl req = getHttpServletRequestImpl(session);
			boolean finished = decode(now, session, req);
			if (finished)
				next(now.slice(), session, req);
			else
				save(now, session);
		}

		public void save(ByteBuffer buf, Session session) {
			if (buf.hasRemaining())
				session.setAttribute("buff", buf);
		}

		public ByteBuffer getBuffer(ByteBuffer buf, Session session) {
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

		public HttpServletRequestImpl getHttpServletRequestImpl(Session session) {
			HttpServletRequestImpl req = (HttpServletRequestImpl) session
					.getAttribute(HTTP_REQUEST);
			if (req == null) {
				req = new HttpServletRequestImpl();
				session.setAttribute(HTTP_REQUEST, req);
			}
			return req;
		}

		public void next(ByteBuffer buf, Session session,
				HttpServletRequestImpl req) throws Throwable {
			req.status++;
			httpDecode[req.status].decode(buf, session, req);
		}
		
		public void clear(Session session) {
			session.removeAttribute("buff");
			session.removeAttribute(HTTP_REQUEST);
		}

		abstract public boolean decode(ByteBuffer buf, Session session,
				HttpServletRequestImpl req) throws Throwable;
	}

	private class RequestLineDecoder extends AbstractHttpDecoder {

		@Override
		public boolean decode(ByteBuffer buf, Session session,
				HttpServletRequestImpl req) throws Throwable {
			int len = buf.remaining();
			for (int i = 0; i < len; i++) {
				if (i <= config.getMaxRequestLineLength()) {
					if (buf.get(i) == LINE_LIMITOR) {
						byte[] data = new byte[i + 1];
						buf.get(data);
						String requestLine = new String(data, config.getEncoding()).trim();
						if(VerifyUtils.isEmpty(requestLine)) {
							log.error("request line length is 0");
							clear(session);
							session.close(true);
							return false;
						}
						
						String[] infos = StringUtils.split(requestLine, ' ');
						if(infos.length > 3) {
							log.error("request line format error: {}", requestLine);
							clear(session);
							session.close(true);
							return false;
						}
						
						req.method = infos[0];
						
						return true;
					}
				} else {
					log.error("request line length is {}, it more than {}", i,
							config.getMaxRequestLineLength());
					clear(session);
					session.close(true);
					return false;
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
			return false;
		}

	}

	private class BodyDecoder extends AbstractHttpDecoder {

		@Override
		public boolean decode(ByteBuffer buf, Session session,
				HttpServletRequestImpl req) throws Throwable {
			// TODO Auto-generated method stub
			return false;
		}

	}

}
