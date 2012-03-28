package com.bhle.access.download;

public interface DownloadRequest {
	public ContentType getContentType();
	
	public String[] getPageURIs();
	
	public Resolution getResolution();
}
