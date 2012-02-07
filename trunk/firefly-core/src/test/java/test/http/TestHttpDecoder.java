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
		byte[] buf1 = "GET /firefly-demo/app/hel".getBytes(config.getEncoding());
		byte[] buf2 = "lo HTTP/1.1\r\nHost: 127.0.0.1\r\n".getBytes(config.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] {ByteBuffer.wrap(buf1), ByteBuffer.wrap(buf2)};
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}
		
		HttpServletRequestImpl req = (HttpServletRequestImpl)session.getAttribute(HttpDecoder.HTTP_REQUEST);
		Assert.assertThat(req.getMethod(), is("GET"));
		Assert.assertThat(req.getRequestURI(), is("/firefly-demo/app/hello"));
		Assert.assertThat(req.getProtocol(), is("HTTP/1.1"));
	}
	
	@Test
	public void testRequestLine2() throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hello HTTP/1.1\r\n".getBytes(config.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] {ByteBuffer.wrap(buf1)};
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}
		
		HttpServletRequestImpl req = (HttpServletRequestImpl)session.getAttribute(HttpDecoder.HTTP_REQUEST);
		Assert.assertThat(req.getMethod(), is("GET"));
		Assert.assertThat(req.getRequestURI(), is("/firefly-demo/app/hello"));
		Assert.assertThat(req.getProtocol(), is("HTTP/1.1"));
	}
	
	@Test
	public void testRequestLine3() throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hel".getBytes(config.getEncoding());
		byte[] buf2 = "lo?query=3.3&test=4 HTTP/1.1\r\nHost: 127.0.0.1\r\n".getBytes(config.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] {ByteBuffer.wrap(buf1), ByteBuffer.wrap(buf2)};
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}
		
		HttpServletRequestImpl req = (HttpServletRequestImpl)session.getAttribute(HttpDecoder.HTTP_REQUEST);
		Assert.assertThat(req.getMethod(), is("GET"));
		Assert.assertThat(req.getRequestURI(), is("/firefly-demo/app/hello"));
		Assert.assertThat(req.getProtocol(), is("HTTP/1.1"));
		Assert.assertThat(req.getQueryString(), is("query=3.3&test=4"));
	}
	
	@Test
	public void testHead() throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hel".getBytes(config.getEncoding());
		byte[] buf2 = "lo?query=3.3&test=4 HTTP/1.1\r\nHost: 127.0.0.1\r\n\r\n".getBytes(config.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] {ByteBuffer.wrap(buf1), ByteBuffer.wrap(buf2)};
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}
		
		HttpServletRequestImpl req = (HttpServletRequestImpl)session.getAttribute(HttpDecoder.HTTP_REQUEST);
		Assert.assertThat(req.getHeader("host"), is("127.0.0.1"));
		Assert.assertThat(req.getHeader("Host"), is("127.0.0.1"));
	}
	

	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hel".getBytes(config.getEncoding());
		byte[] buf2 = "lo HTTP/1.1\r\nHost:127.0.0.1\r\nAccept-Language:zh-CN,".getBytes(config.getEncoding());
		byte[] buf3 = "zh;q=0.8\r\nConnection:keep-alive\r\n\r\n".getBytes(config.getEncoding());
		System.out.println("Host:127.0.0.1\r\nAccept-Language:zh-CN,".length());
		System.out.println("Accept-Language:zh-CN,".length());
		System.out.println("zh;q=0.8\r\nConnection:keep-alive\r\n\r\n".length());
		System.out.println("===================================================");
		
		
		ByteBuffer[] buf = new ByteBuffer[] {ByteBuffer.wrap(buf1), ByteBuffer.wrap(buf2), ByteBuffer.wrap(buf3)};
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}
		
		HttpServletRequestImpl req = (HttpServletRequestImpl)session.getAttribute(HttpDecoder.HTTP_REQUEST);
		System.out.println(req.getMethod());
		System.out.println(req.getRequestURI());
		System.out.println(req.getProtocol());
		System.out.println(req.getHeader("Host"));
		System.out.println(req.getHeader("Accept-Language"));
		System.out.println(req.getHeader("Connection"));
	}

}
