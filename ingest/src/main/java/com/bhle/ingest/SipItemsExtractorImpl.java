package com.bhle.ingest;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.bhle.ingest.integration.FilenameRegexFilter;

public class SipItemsExtractorImpl implements SipItemsExtractor {

	public FilenameRegexFilter filter;
	
	public void setFilter(FilenameRegexFilter filter) {
		this.filter = filter;
	}

	@Override
	public List<File> getItems(Sip sip) {
		File sipDirectory = new File(sip.getURI());
		File[] sipItems = sipDirectory.listFiles(filter);
		return Arrays.asList(sipItems);
	}

}
