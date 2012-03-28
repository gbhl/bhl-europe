package com.bhle.access.download.generator;


public interface PageURIExtractor {
	public String[] getPageURIs(String guid, String rangesParameter);
}
