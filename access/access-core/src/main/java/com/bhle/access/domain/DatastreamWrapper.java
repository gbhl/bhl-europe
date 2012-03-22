package com.bhle.access.domain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class DatastreamWrapper {
	private InputStream inputStream;
	private DigitalObjectWrapper digitalObject;
	private String dsid;
	private String mimeType;

	private byte[] data;

	public DatastreamWrapper(String dsid) {
		this.dsid = dsid;
	}

	// Buffer Datastream for multiple reads
	public InputStream getInputStream() {
		// not buffered yet
		if (data == null) {
			try {
				data = IOUtils.toByteArray(inputStream);
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return new ByteArrayInputStream(data);
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
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

	public void close() {
		data = null;
	}

}
