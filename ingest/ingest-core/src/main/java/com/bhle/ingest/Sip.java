package com.bhle.ingest;

import java.io.File;
import java.net.URI;
import java.util.List;

public class Sip {
	public static final String JOB_PARAM_GUID_KEY = "GUID";
	public static final String JOB_PARAM_URI_KEY = "URI";

	private static SipItemsExtractor extractor;

	public void setExtractor(SipItemsExtractor extractor) {
		Sip.extractor = extractor;
	}

	private String guid;
	private URI uri;

	private List<File> items;

	public Sip() {
	}

	public Sip(String guid, URI uri) {
		this.guid = guid;
		this.uri = uri;
	}

	public URI getURI() {
		return uri;
	}

	public String getGuid() {
		return guid;
	}

	public URI getUri() {
		return uri;
	}

	public List<File> getItems() {
		if (this.items == null) {
			this.items = extractor.getItems(this);
		}
		return items;
	}
}
