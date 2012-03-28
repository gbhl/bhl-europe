package com.bhle.access.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class RereadabelBufferedInputStream extends BufferedInputStream {

	public RereadabelBufferedInputStream(InputStream in) {
		super(in);
		this.mark(Integer.MAX_VALUE);
	}

	@Override
	public synchronized int read() throws IOException {
		int result = super.read();
		if (result == -1) {
			this.reset();
		}
		return result;
	}

	@Override
	public void close() throws IOException {
		this.reset();
	}

	public void closeCompletely() throws IOException {
		this.close();
	}
}
