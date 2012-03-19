package com.bhle.ingest.integration;

import java.io.File;
import java.io.FilenameFilter;

public class FilenameRegexFilter implements FilenameFilter {

	private String regex;
	
	public FilenameRegexFilter(String regex) {
		this.regex = regex;
	}
	
	@Override
	public boolean accept(File dir, String name) {
		return name.matches(regex);
	}

}
