package com.bhle.access.download.offline;

import com.bhle.access.download.Resolution;

public class RawRequest {
	private String email;

	private String guid;
	private String ranges;
	private String resolution;
	
	private String format;

	public RawRequest() {
	}
	
	public RawRequest(Object[] args) {
		this.guid = (String) args[0];
		this.ranges = (String) args[1];
		Resolution resolution = (Resolution) args[2];
		this.resolution = resolution.getResolution();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public void setRanges(String ranges) {
		this.ranges = ranges;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public String getGuid() {
		return guid;
	}

	public String getRanges() {
		return ranges;
	}

	public String getResolution() {
		return resolution;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
}
