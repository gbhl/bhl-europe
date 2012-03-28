package com.bhle.access.download;


public class BasicDownloadResponse implements DownloadResponse {

	DownloadRequest request;
	public DownloadRequest getRequest() {
		return request;
	}
	public void setRequest(DownloadRequest request) {
		this.request = request;
	}
}
