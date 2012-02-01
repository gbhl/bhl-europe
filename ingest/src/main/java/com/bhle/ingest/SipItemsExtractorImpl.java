package com.bhle.ingest;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.file.filters.AbstractFileListFilter;
import org.springframework.stereotype.Component;

import com.bhle.ingest.integration.FilenameSuffixFilter;

@Component
public class SipItemsExtractorImpl implements SipItemsExtractor {

	public FilenameSuffixFilter filter = new FilenameSuffixFilter(".xml");

	@Override
	public List<File> getItems(Sip sip) {
		File sipDirectory = new File(sip.getURI());
		File[] sipItems = sipDirectory.listFiles(filter);
		return Arrays.asList(sipItems);
	}

	class FilenameSuffixFilter implements FilenameFilter {

		private String suffix;

		public FilenameSuffixFilter(String suffix) {
			this.suffix = suffix;
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(suffix);
		}

	}

}
