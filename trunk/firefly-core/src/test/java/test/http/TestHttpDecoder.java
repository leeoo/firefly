package test.http;

import static org.hamcrest.Matchers.*;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Enumeration;

import org.junit.Assert;
import org.junit.Test;

import com.firefly.server.http.Config;
import com.firefly.server.http.HttpDecoder;
import com.firefly.server.http.HttpServletRequestImpl;
import com.firefly.server.http.HttpServletResponseImpl;

public class TestHttpDecoder {
	private static final Config config = new Config();
	private static final HttpDecoder httpDecoder = new HttpDecoder(config);

	@Test
	public void testRequestLine() throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hel"
				.getBytes(config.getEncoding());
		byte[] buf2 = "lo HTTP/1.1\r\nHost: 127.0.0.1\r\n\r\n".getBytes(config
				.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1),
				ByteBuffer.wrap(buf2) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}

		HttpServletRequestImpl req = session.request;
		Assert.assertThat(req.getMethod(), is("GET"));
		Assert.assertThat(req.getRequestURI(), is("/firefly-demo/app/hello"));
		Assert.assertThat(req.getProtocol(), is("HTTP/1.1"));
	}

	@Test
	public void testRequestLine2() throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hello HTTP/1.1\r\n\r\n"
				.getBytes(config.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}

		HttpServletRequestImpl req = session.request;
		Assert.assertThat(req.getMethod(), is("GET"));
		Assert.assertThat(req.getRequestURI(), is("/firefly-demo/app/hello"));
		Assert.assertThat(req.getProtocol(), is("HTTP/1.1"));
	}

	@Test
	public void testRequestLine3() throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hello?query=3.3&test=4 HTTP/1.1\r\nHost: 127.0.0.1\r\n\r\n"
				.getBytes(config.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}

		HttpServletRequestImpl req = session.request;
		Assert.assertThat(req.getMethod(), is("GET"));
		Assert.assertThat(req.getRequestURI(), is("/firefly-demo/app/hello"));
		Assert.assertThat(req.getProtocol(), is("HTTP/1.1"));
		Assert.assertThat(req.getQueryString(), is("query=3.3&test=4"));
	}

	@Test
	public void testHead() throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hel"
				.getBytes(config.getEncoding());
		byte[] buf2 = "lo?query=3.3&test=4 HTTP/1.1\r\nHost: 127.0.0.1\r\n\r\n"
				.getBytes(config.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1),
				ByteBuffer.wrap(buf2) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}

		HttpServletRequestImpl req = session.request;
		Assert.assertThat(req.getHeader("host"), is("127.0.0.1"));
		Assert.assertThat(req.getHeader("Host"), is("127.0.0.1"));
	}

	@Test
	public void testHead2() throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hel"
				.getBytes(config.getEncoding());
		byte[] buf2 = "lo HTTP/1.1\r\nHost:127.0.0.1\r\nAccept-Language:zh-CN,"
				.getBytes(config.getEncoding());
		byte[] buf3 = "zh;q=0.8\r\nConnection:keep-alive\r\n\r\n"
				.getBytes(config.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1),
				ByteBuffer.wrap(buf2), ByteBuffer.wrap(buf3) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}

		HttpServletRequestImpl req = session.request;
		Assert.assertThat(req.getHeader("host"), is("127.0.0.1"));
		Assert.assertThat(req.getHeader("connection"), is("keep-alive"));
		Assert.assertThat(req.getHeader("Accept-Language"),
				is("zh-CN,zh;q=0.8"));
	}

	@Test
	public void testHead3() throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hello HTTP/1.1\r\n"
				.getBytes(config.getEncoding());
		byte[] buf2 = "Host:127.0.0.1\r\n".getBytes(config.getEncoding());
		byte[] buf3 = "Accept-Language:zh-CN,zh;q=0.8\r\nConnection:keep-alive\r\n"
				.getBytes(config.getEncoding());
		byte[] buf4 = "Accept-Encoding: gzip,deflate,sdch\r\n\r\n"
				.getBytes(config.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1),
				ByteBuffer.wrap(buf2), ByteBuffer.wrap(buf3),
				ByteBuffer.wrap(buf4) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}

		HttpServletRequestImpl req = session.request;
		Assert.assertThat(req.getHeader("host"), is("127.0.0.1"));
		Assert.assertThat(req.getHeader("connection"), is("keep-alive"));
		Assert.assertThat(req.getHeader("Accept-Language"),
				is("zh-CN,zh;q=0.8"));
		Assert.assertThat(req.getHeader("Accept-Encoding"),
				is("gzip,deflate,sdch"));
	}

	@Test
	public void testHead4() throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hello HTTP/1.1\r\nHost:127.0.0.1\r\nAccept-Language:zh-CN,zh;q=0.8\r\nConnection:keep-alive\r\nAccept-Encoding: gzip,deflate,sdch\r\n\r\n"
				.getBytes(config.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}

		HttpServletRequestImpl req = session.request;
		Assert.assertThat(req.getMethod(), is("GET"));
		Assert.assertThat(req.getRequestURI(), is("/firefly-demo/app/hello"));
		Assert.assertThat(req.getProtocol(), is("HTTP/1.1"));
		Assert.assertThat(req.getHeader("host"), is("127.0.0.1"));
		Assert.assertThat(req.getHeader("connection"), is("keep-alive"));
		Assert.assertThat(req.getHeader("Accept-Language"),
				is("zh-CN,zh;q=0.8"));
		Assert.assertThat(req.getHeader("Accept-Encoding"),
				is("gzip,deflate,sdch"));
	}
	
	@Test
	public void testBody() throws Throwable {
		byte[] buf1 = "POST /firefly-demo/app/hel".getBytes(config
				.getEncoding());
		byte[] buf2 = "lo HTTP/1.1\r\nHost:127.0.0.1\r\nAccept-Language:zh-CN,"
				.getBytes(config.getEncoding());
		byte[] buf3 = "zh;q=0.8\r\nConnection:keep-alive\r\n".getBytes(config
				.getEncoding());
		byte[] buf4 = "Accept-Encoding:gzip,deflate,sdch\r\nContent-Type:app"
				.getBytes(config.getEncoding());
		byte[] buf5 = "lication/x-www-form-urlencoded\r\nContent-Length:34\r\n\r\n"
				.getBytes(config.getEncoding());
		byte[] buf6 = "title=%E6%B5%8B%E8%AF%95&price=3.3".getBytes(config
				.getEncoding());

		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1),
				ByteBuffer.wrap(buf2), ByteBuffer.wrap(buf3),
				ByteBuffer.wrap(buf4), ByteBuffer.wrap(buf5),
				ByteBuffer.wrap(buf6) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}
		
		HttpServletRequestImpl req = session.request;
		Assert.assertThat(req.getParameter("title"), is("测试"));
		Assert.assertThat(req.getParameter("price"), is("3.3"));
	}
	
	@Test
	public void testBody2() throws Throwable {
		byte[] buf1 = "POST /firefly-demo/app/hel".getBytes(config
				.getEncoding());
		byte[] buf2 = "lo HTTP/1.1\r\nHost:127.0.0.1\r\nAccept-Language:zh-CN,"
				.getBytes(config.getEncoding());
		byte[] buf3 = "zh;q=0.8\r\nConnection:keep-alive\r\n".getBytes(config
				.getEncoding());
		byte[] buf4 = "Accept-Encoding:gzip,deflate,sdch\r\nContent-Type:app"
				.getBytes(config.getEncoding());
		byte[] buf5 = "lication/x-www-form-urlencoded\r\nContent-Length:31\r\n\r\n"
				.getBytes(config.getEncoding());
		byte[] buf6 = "title=%E6%B5%8B%E8%AF%95&price=".getBytes(config
				.getEncoding());

		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1),
				ByteBuffer.wrap(buf2), ByteBuffer.wrap(buf3),
				ByteBuffer.wrap(buf4), ByteBuffer.wrap(buf5),
				ByteBuffer.wrap(buf6) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}
		
		HttpServletRequestImpl req = session.request;
		Assert.assertThat(req.getParameter("title"), is("测试"));
		Assert.assertThat(req.getParameter("price"), is(""));
	}
	
	@Test
	public void testBody3() throws Throwable {
		byte[] buf1 = "POST /firefly-demo/app/hel".getBytes(config
				.getEncoding());
		byte[] buf2 = "lo HTTP/1.1\r\nHost:127.0.0.1\r\nAccept-Language:zh-CN,"
				.getBytes(config.getEncoding());
		byte[] buf3 = "zh;q=0.8\r\nConnection:keep-alive\r\n".getBytes(config
				.getEncoding());
		byte[] buf4 = "Accept-Encoding:gzip,deflate,sdch\r\nContent-Type:app"
				.getBytes(config.getEncoding());
		byte[] buf5 = "lication/x-www-form-urlencoded\r\nContent-Length:24\r\n\r\n"
				.getBytes(config.getEncoding());
		byte[] buf6 = "title=%E6%B5%8B%E8%AF%95".getBytes(config
				.getEncoding());

		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1),
				ByteBuffer.wrap(buf2), ByteBuffer.wrap(buf3),
				ByteBuffer.wrap(buf4), ByteBuffer.wrap(buf5),
				ByteBuffer.wrap(buf6) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}
		
		HttpServletRequestImpl req = session.request;
		Assert.assertThat(req.getParameter("title"), is("测试"));
		Assert.assertThat(req.getParameter("price"), nullValue());
		Assert.assertThat(req.getContentLength(), is(24));
		Assert.assertThat(req.getContentType(), is("application/x-www-form-urlencoded"));
	}

	public static void main(String[] args) throws Throwable {
		test2();
	}

	public static void test2() throws Throwable {
		byte[] buf1 = "POST /firefly-demo/app/hel".getBytes(config
				.getEncoding());
		byte[] buf2 = "lo HTTP/1.1\r\nHost:127.0.0.1\r\nAccept-Language:zh-CN,"
				.getBytes(config.getEncoding());
		byte[] buf3 = "zh;q=0.8\r\nConnection:keep-alive\r\n".getBytes(config
				.getEncoding());
		byte[] buf4 = "Accept-Encoding:gzip,deflate,sdch\r\nContent-Type:app"
				.getBytes(config.getEncoding());
		byte[] buf5 = "lication/x-www-form-urlencoded\r\nContent-Length:24\r\n\r\n"
				.getBytes(config.getEncoding());
		byte[] buf6 = "title=%E6%B5%8B%E8%AF%95".getBytes(config
				.getEncoding());

		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1),
				ByteBuffer.wrap(buf2), ByteBuffer.wrap(buf3),
				ByteBuffer.wrap(buf4), ByteBuffer.wrap(buf5),
				ByteBuffer.wrap(buf6) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}
		
		HttpServletRequestImpl req = session.request;
		System.out.println(req.getMethod());
		System.out.println(req.getContentType());
		System.out.println(req.getContentLength());
		System.out.println(req.getParameter("title"));
		System.out.println(req.getParameter("price"));
	}

	public static void test1() throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hel"
				.getBytes(config.getEncoding());
		byte[] buf2 = "lo HTTP/1.1\r\nHost:127.0.0.1\r\nAccept-Language:zh-CN,"
				.getBytes(config.getEncoding());
		byte[] buf3 = "zh;q=0.8\r\nConnection:keep-alive\r\n".getBytes(config
				.getEncoding());
		byte[] buf4 = "Accept-Encoding:gzip,deflate,sdch\r\n\r\n"
				.getBytes(config.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1),
				ByteBuffer.wrap(buf2), ByteBuffer.wrap(buf3),
				ByteBuffer.wrap(buf4) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}
		HttpServletRequestImpl req = session.request;
		System.out.println(req.getMethod());
		System.out.println(req.getRequestURI());
		System.out.println(req.getProtocol());
		System.out.println(req.getHeader("Host"));
		System.out.println(req.getHeader("Accept-Language"));
		System.out.println(req.getHeader("Connection"));
		System.out.println(req.getHeader("Accept-Encoding"));
		System.out.println(req.toString());

		Enumeration<String> enumeration = req.getHeaders("Accept-Encoding");
		while (enumeration.hasMoreElements()) {
			System.out.println(">>" + enumeration.nextElement());
		}

		System.out.println(HttpServletResponseImpl.GMT_FORMAT
				.format(new Date()));
	}

}
