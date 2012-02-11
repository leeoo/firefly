package com.firefly.server.io;

import java.io.File;
import java.io.IOException;

import com.firefly.server.http.HttpServletResponseImpl;

public class StaticFileOutputStream extends HttpServerOutpuStream {

	public StaticFileOutputStream(int bufferSize,
			NetBufferedOutputStream bufferedOutput,
			HttpServletResponseImpl response) {
		super(bufferSize, bufferedOutput, response);
	}

	public void write(File file, long off, long len) throws IOException {
		if (len > bufferSize) {
			flush();
			bufferedOutput.write(file, off, len);
			return;
		}

		queue.offer(new FileChunkedData(file, off, len));
		size += len;
		if (size > bufferSize)
			flush();
	}

	public void write(File file) throws IOException {
		write(file, 0, file.length());
	}

	private class FileChunkedData extends ChunkedData {

		private long off, len;
		private File file;

		public FileChunkedData(File file, long off, long len) {
			super();
			this.off = off;
			this.len = len;
			this.file = file;
		}

		@Override
		void write() throws IOException {
			bufferedOutput.write(file, off, len);
		}

	}

}
