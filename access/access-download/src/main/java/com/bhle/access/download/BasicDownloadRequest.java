package com.bhle.access.download;

import org.akubraproject.Blob;

import com.bhle.access.util.Resolution;

public class BasicDownloadRequest implements DownloadRequest {

	ContentType contentType;
	String[] pageURIs;
	Resolution resolution;

	Blob blob;
	boolean isOffline;

	public BasicDownloadRequest() {
	}

	public BasicDownloadRequest(ContentType contentType, String[] pageURIs,
			Resolution resolution) {
		super();
		this.contentType = contentType;
		this.pageURIs = pageURIs;
		this.resolution = resolution;
	}

	@Override
	public ContentType getContentType() {
		return contentType;
	}

	@Override
	public String[] getPageURIs() {
		return pageURIs;
	}

	@Override
	public Resolution getResolution() {
		return resolution;
	}

	@Override
	public Blob getBlob() {
		return blob;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public void setPageURIs(String[] pageURIs) {
		this.pageURIs = pageURIs;
	}

	public void setResolution(Resolution resolution) {
		this.resolution = resolution;
	}

	public void setBlob(Blob blob) {
		this.blob = blob;
	}

	@Override
	public boolean isOffline() {
		return isOffline;
	}

}
