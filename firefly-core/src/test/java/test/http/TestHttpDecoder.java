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
		byte[] buf2 = "lo HTTP/1.1\ndfsdfsdf".getBytes(config.getEncoding());
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
		byte[] buf1 = "GET /firefly-demo/app/hello HTTP/1.1\n".getBytes(config.getEncoding());
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
	

	/**
	 * @param args
	 * @throws Throwable 
	 */
	public static void main(String[] args) throws Throwable {
		byte[] buf1 = "GET /firefly-demo/app/hel".getBytes(config.getEncoding());
		byte[] buf2 = "lo HTTP/1.1\ntestNext".getBytes(config.getEncoding());
		ByteBuffer[] buf = new ByteBuffer[] {ByteBuffer.wrap(buf1), ByteBuffer.wrap(buf2)};
		MockSession session = new MockSession();

		for (int i = 0; i < buf.length; i++) {
			httpDecoder.decode(buf[i], session);
		}
		
		HttpServletRequestImpl req = (HttpServletRequestImpl)session.getAttribute(HttpDecoder.HTTP_REQUEST);
		System.out.println(req.getMethod());
		System.out.println(req.getRequestURI());
		System.out.println(req.getProtocol());
	}

}
