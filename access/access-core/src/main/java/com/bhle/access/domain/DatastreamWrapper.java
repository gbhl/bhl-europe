package com.bhle.access.domain;

import java.io.InputStream;

import com.bhle.access.util.RereadabelBufferedInputStream;

public class DatastreamWrapper {
	private InputStream inputStream;
	private DigitalObjectWrapper digitalObject;
	private String dsid;
	private String mimeType;

	public DatastreamWrapper(String dsid) {
		this.dsid = dsid;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	// Buffer Datastream for multiple reads
	public void setInputStream(InputStream inputStream) {
		this.inputStream = new RereadabelBufferedInputStream(inputStream);
	}

	public DigitalObjectWrapper getDigitalObject() {
		return digitalObject;
	}

	public void setDigitalObject(DigitalObjectWrapper digitalObject) {
		this.digitalObject = digitalObject;
	}

	public String getDsid() {
		return dsid;
	}

	public void setDsid(String dsid) {
		this.dsid = dsid;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

}
