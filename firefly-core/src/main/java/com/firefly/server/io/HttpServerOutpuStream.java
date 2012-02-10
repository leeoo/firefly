package com.firefly.server.io;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import javax.servlet.ServletOutputStream;

import com.firefly.server.http.HttpServletResponseImpl;

public class HttpServerOutpuStream extends ServletOutputStream {
	protected Queue<ChunkedData> queue = new LinkedList<ChunkedData>();
	protected int size, bufferSize;
	protected NetBufferedOutputStream bufferedOutput;
	protected HttpServletResponseImpl response;

	public HttpServerOutpuStream(int bufferSize,
			NetBufferedOutputStream bufferedOutput,
			HttpServletResponseImpl response) {
		this.bufferSize = bufferSize;
		this.bufferedOutput = bufferedOutput;
		this.response = response;
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
	public void print(String s) throws IOException {
		if (s == null)
			s = "null";
		if (s.length() > 0)
			write(response.stringToByte(s));
	}

	@Override
	public void flush() throws IOException {

	}

	@Override
	public void close() throws IOException {
		if (!response.isCommitted()) {
			response.setHeader("Content-Length", String.valueOf(size));
			bufferedOutput.write(response.getHeadData());
			response.setCommitted(true);
		}

		if (size > 0) {
			for (ChunkedData d = null; (d = queue.poll()) != null;)
				d.write();

			size = 0;
			bufferedOutput.close();
		}
	}

	public void resetBuffer() {
		bufferedOutput.resetBuffer();
		size = 0;
		queue.clear();
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

	abstract protected class ChunkedData {
		abstract void write() throws IOException;
	}
}
