package com.bhle.access.download;

import org.akubraproject.Blob;

import com.bhle.access.util.Resolution;

public interface DownloadRequest {
	public ContentType getContentType();
	
	public String[] getPageURIs();
	
	public Resolution getResolution();
	
	public Blob getBlob();
	
	public boolean isOffline();
}
