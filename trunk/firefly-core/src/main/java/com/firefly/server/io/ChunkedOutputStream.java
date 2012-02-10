package com.firefly.server.io;

import java.io.IOException;
import com.firefly.server.http.HttpServletResponseImpl;

public class ChunkedOutputStream extends HttpServerOutpuStream {

	private byte[] crlf, endFlag;

	public ChunkedOutputStream(int bufferSize,
			NetBufferedOutputStream bufferedOutput,
			HttpServletResponseImpl response) {
		super(bufferSize, bufferedOutput, response);
		crlf = response.stringToByte("\r\n");
		endFlag = response.stringToByte("0\r\n\r\n");
	}

	@Override
	public void flush() throws IOException {
		if (!response.isCommitted()) {
			response.setHeader("Transfer-Encoding", "chunked");
			bufferedOutput.write(response.getHeadData());
			response.setCommitted(true);
		}

		if (size > 0) {
			bufferedOutput.write(response.getChunkedSize(size));
			for (ChunkedData d = null; (d = queue.poll()) != null;) {
				d.write();
			}
			bufferedOutput.write(crlf);
			size = 0;
		}
	}

	@Override
	public void close() throws IOException {
		flush();
		bufferedOutput.write(endFlag);
		bufferedOutput.close();
	}

}
