package com.bhle.access.download;

import org.akubraproject.Blob;

public class OfflineDownloadResponse extends BasicDownloadResponse{
	private Blob blob;

	public Blob getBlob() {
		return blob;
	}

	public void setBlob(Blob blob) {
		this.blob = blob;
	}
}
