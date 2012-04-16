package com.bhle.access.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatastreamWrapper {
	
	private static final Logger logger = LoggerFactory
			.getLogger(DatastreamWrapper.class);
	
	private DigitalObjectWrapper digitalObject;
	private String dsid;
	private String mimeType;

	private File tmp;
	private InputStream inputStream;

	public DatastreamWrapper(String dsid) {
		this.dsid = dsid;
	}

	public InputStream getInputStream() {
		if (tmp == null) {
			try {
				tmp = File.createTempFile("datastream", null);
				FileOutputStream out = new FileOutputStream(tmp);
				IOUtils.copy(inputStream, out);
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			return new FileInputStream(tmp);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
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
		IOUtils.closeQuietly(inputStream);
		if (tmp != null) {
			if (!tmp.delete() && tmp.exists()) {
				logger.warn("Cannot delete temp files {}" , tmp);
			}
		}
	}

}
