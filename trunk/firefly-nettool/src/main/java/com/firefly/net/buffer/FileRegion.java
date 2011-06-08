package com.firefly.net.buffer;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRegion {
	private static Logger log = LoggerFactory.getLogger(FileRegion.class);
	private final FileChannel file;
	private final long position;
	private final long count;

	public FileRegion(FileChannel file, long position, long count) {
		this.file = file;
		this.position = position;
		this.count = count;
	}

	public long getPosition() {
		return position;
	}

	public long getCount() {
		return count;
	}

	public long transferTo(WritableByteChannel target, long position)
			throws IOException {
		long count = this.count - position;
		if (count < 0 || position < 0) {
			throw new IllegalArgumentException("position out of range: "
					+ position + " (expected: 0 - " + (this.count - 1) + ")");
		}
		if (count == 0) {
			return 0L;
		}

		return file.transferTo(this.position + position, count, target);
	}

	public void releaseExternalResources() {
		try {
			log.debug("FileChannel close");
			file.close();
		} catch (IOException e) {
			log.error("Failed to close a file.", e);
		}
	}
}
