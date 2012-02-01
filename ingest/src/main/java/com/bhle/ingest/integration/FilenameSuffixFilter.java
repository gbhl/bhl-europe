package com.bhle.ingest.integration;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.file.filters.AbstractFileListFilter;

public class FilenameSuffixFilter extends AbstractFileListFilter<File> {

	private static final Logger logger = LoggerFactory
			.getLogger(FilenameSuffixFilter.class);
	
	private String suffix;
	
	public FilenameSuffixFilter(String suffix) {
		this.suffix = suffix;
	}
	
	@Override
	protected boolean accept(File file) {
		logger.info(file.getAbsolutePath());
		return file.getName().endsWith(suffix);
	}

}
