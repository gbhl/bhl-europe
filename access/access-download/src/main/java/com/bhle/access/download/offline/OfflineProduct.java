package com.bhle.access.download.offline;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

public class OfflineProduct {

	private String guid;
	private String suffix;

	private Date timestamp;

	private byte[] byteStream;
	private String email;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public byte[] getByteStream() {
		return byteStream;
	}

	public void setByteStream(byte[] byteStream) {
		this.byteStream = byteStream;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFileName() {
		return guid + "_" + timestamp.getTime() + "." + suffix;
	}

	public InputStream getInputStream() {
		return new ByteArrayInputStream(byteStream);
	}
}
