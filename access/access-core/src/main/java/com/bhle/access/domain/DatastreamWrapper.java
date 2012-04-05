package com.bhle.access.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class DatastreamWrapper {
	private DigitalObjectWrapper digitalObject;
	private String dsid;
	private String mimeType;

	private File tmp;

	public DatastreamWrapper(String dsid) {
		this.dsid = dsid;
	}

	public InputStream getInputStream() {
		try {
			return new FileInputStream(tmp);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setInputStream(InputStream inputStream) {
		try {
			tmp = File.createTempFile("bhle", null);
			FileOutputStream out = new FileOutputStream(tmp);
			IOUtils.copy(inputStream, out);
			out.close();
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		tmp.delete();
	}

}
