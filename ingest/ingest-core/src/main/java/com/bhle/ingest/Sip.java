package com.bhle.ingest;

import java.io.File;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class Sip {
	
	public static final String JOB_PARAM_URI_KEY = "SIP_URI";
	
	private static SipItemsExtractor extractor;
	
	public void setExtractor(SipItemsExtractor extractor) {
		Sip.extractor = extractor;
	}
	
	private URI uri;
	
	private List<File> items;

	public Sip() {
	}
	
	public Sip(URI uri) {
		this.uri = uri;
	}

	public URI getURI() {
		return uri;
	}

	public void setURI(URI uri) {
		this.uri = uri;
	}

	public List<File> getItems() {
		if (this.items == null){
			this.items = extractor.getItems(this);
		}
		return items;
	}
}
