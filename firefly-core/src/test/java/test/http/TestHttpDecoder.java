package test.http;

import java.nio.ByteBuffer;

import static org.hamcrest.Matchers.*;

import org.junit.Assert;
import org.junit.Test;

import com.firefly.server.http.Config;
import com.firefly.server.http.HttpDecoder;
import com.firefly.server.http.HttpServletRequestImpl;

public class TestHttpDecoder {
	private static final Config config = new Config();
	private static final HttpDecoder httpDecoder = new HttpDecoder(config);

	@Test
	public void testRequestLine() throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hel"
				.getBytes(config.getEncoding());
		byte[] buf2 = "lo HTTP/1.1\r\nHost: 127.0.0.1\r\n".getBytes(config
				.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1),
				ByteBuffer.wrap(buf2) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}

		HttpServletRequestImpl req = (HttpServletRequestImpl) session
				.getAttribute(HttpDecoder.HTTP_REQUEST);
		Assert.assertThat(req.getMethod(), is("GET"));
		Assert.assertThat(req.getRequestURI(), is("/firefly-demo/app/hello"));
		Assert.assertThat(req.getProtocol(), is("HTTP/1.1"));
	}

	@Test
	public void testRequestLine2() throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hello HTTP/1.1\r\n"
				.getBytes(config.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}

		HttpServletRequestImpl req = (HttpServletRequestImpl) session
				.getAttribute(HttpDecoder.HTTP_REQUEST);
		Assert.assertThat(req.getMethod(), is("GET"));
		Assert.assertThat(req.getRequestURI(), is("/firefly-demo/app/hello"));
		Assert.assertThat(req.getProtocol(), is("HTTP/1.1"));
	}

	@Test
	public void testRequestLine3() throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hello?query=3.3&test=4 HTTP/1.1\r\nHost: 127.0.0.1\r\n"
				.getBytes(config.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] { ByteBuffer.wrap(buf1) };
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}

		HttpServletRequestImpl req = (HttpServletRequestImpl) session
				.getAttribute(HttpDecoder.HTTP_REQUEST);
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

		HttpServletRequestImpl req = (HttpServletRequestImpl) session
				.getAttribute(HttpDecoder.HTTP_REQUEST);
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

		HttpServletRequestImpl req = (HttpServletRequestImpl) session
				.getAttribute(HttpDecoder.HTTP_REQUEST);
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

		HttpServletRequestImpl req = (HttpServletRequestImpl) session
				.getAttribute(HttpDecoder.HTTP_REQUEST);
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

		HttpServletRequestImpl req = (HttpServletRequestImpl) session
				.getAttribute(HttpDecoder.HTTP_REQUEST);
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

	public static void main(String[] args) throws Throwable {
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
		HttpServletRequestImpl req = (HttpServletRequestImpl) session
				.getAttribute(HttpDecoder.HTTP_REQUEST);
		System.out.println(req.getMethod());
		System.out.println(req.getRequestURI());
		System.out.println(req.getProtocol());
		System.out.println(req.getHeader("Host"));
		System.out.println(req.getHeader("Accept-Language"));
		System.out.println(req.getHeader("Connection"));
		System.out.println(req.getHeader("Accept-Encoding"));
		System.out.println(req.toString());
	}

}
