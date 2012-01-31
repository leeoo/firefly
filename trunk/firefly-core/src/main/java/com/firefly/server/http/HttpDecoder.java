package com.firefly.server.http;

import java.nio.ByteBuffer;

import com.firefly.net.Decoder;
import com.firefly.net.Session;

public class HttpDecoder implements Decoder {
	private AbstractHttpDecoder[] httpDecode;

	@Override
	public void decode(ByteBuffer buf, Session session) throws Throwable {
		httpDecode[0].decode(buf, session);
	}

	public abstract class AbstractHttpDecoder implements Decoder {
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
					.getAttribute("http_req");
			if (req == null) {
				req = new HttpServletRequestImpl();
				session.setAttribute("http_req", req);
			}
			return req;
		}

		public void next(ByteBuffer buf, Session session,
				HttpServletRequestImpl req) throws Throwable {
			req.status++;
			httpDecode[req.status].decode(buf, session, req);
		}
		
		public void clear(Session session) {
			session.removeAttribute("http_req");
			session.removeAttribute("buff");
		}

		abstract public boolean decode(ByteBuffer buf, Session session,
				HttpServletRequestImpl req) throws Throwable;
	}

}
