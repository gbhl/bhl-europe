package com.atos.bookingester.rebuild;

public class DataStream {
	private String mimeType;
	private String location;
	private DatastreamContentType type;
	private Object content;
	private String label;

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public DatastreamContentType getType() {
		return type;
	}

	public void setType(DatastreamContentType type) {
		this.type = type;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
