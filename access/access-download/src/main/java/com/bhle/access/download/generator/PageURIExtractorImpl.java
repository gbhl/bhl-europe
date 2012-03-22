package com.bhle.access.download.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.FedoraUtil;

public class PageURIExtractorImpl implements PageURIExtractor {

	private static final Logger logger = LoggerFactory
			.getLogger(PageURIExtractorImpl.class);

	// private static String INDEX_REGEX = "n\\d+|n\\d+-n\\d+";
	// private static String PAGE_NAME_REGEX = "\\w+|\\w+-\\w+";

	public String[] getPageURIs(String rangesParameter, String[] allPageUris) {
		List<String> pageURIs = new ArrayList<String>();

		String[] ranges = splitRanges(rangesParameter);

		// No delimiter
		if (ranges.length < 2) {
			return allPageUris;
		} else {
			logger.debug("Number of ranges: {}", ranges.length);
			for (String range : ranges) {
				String[] pageUriFromRange = transformRanges(range, allPageUris);
				pageURIs.addAll(Arrays.asList(pageUriFromRange));
			}
		}

		return pageURIs.toArray(new String[pageURIs.size()]);
	}

	public String[] getPageURIs(String guid, String rangesParameter) {

		String[] allPageUris = FedoraUtil.getAllMembers(
				FedoraURI.getPidFromGuid(guid), FedoraUtil.PAGE_MODEL);

		Arrays.sort(allPageUris);

		return getPageURIs(rangesParameter, allPageUris);
	}

	private String[] splitRanges(String rangesParameter) {
		return rangesParameter.split(",");
	}

	private String[] transformRanges(String range, String[] allPageUris) {
		if (PageIndexParser.isValid(range)) {
			return PageIndexParser.parse(range, allPageUris);
		}
		// else if (isPageName(range)) {
		// TODO: not implemented yet
		// return PageNameParser.parse(range, pageUris);
		// }

		throw new IllegalArgumentException("range is not valid");
	}

}
