package com.bhle.access.download;

public class OnlineDownloadResponse extends BasicDownloadResponse {
	private byte[] bytes;

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
}
