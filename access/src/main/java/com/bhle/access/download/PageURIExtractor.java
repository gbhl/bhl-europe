package com.bhle.access.download;


public interface PageURIExtractor {
	public String[] getPageURIs(String guid, String rangesParameter);
}
