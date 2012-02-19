package com.bhle.access.download;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.FedoraUtil;

public class PageURIExtractorImpl implements PageURIExtractor {
	
	private static final Logger logger = LoggerFactory
			.getLogger(PageURIExtractorImpl.class);

//	private static String INDEX_REGEX = "n\\d+|n\\d+-n\\d+";
//	private static String PAGE_NAME_REGEX = "\\w+|\\w+-\\w+";

	private String[] pageUris;

	public String[] getPageURIs(String guid, String rangesParameter) {
		
		List<String> pageURIs = new ArrayList<String>();

		pageUris = FedoraUtil.getAllMembers(FedoraURI.getPidFromGuid(guid),
				FedoraUtil.PAGE_MODEL);
		Arrays.sort(pageUris);

		String[] ranges = splitRanges(rangesParameter);

		if (ranges.length < 2) {
			return pageUris;
		} else {
			for (String range : ranges) {
				String[] pageUriFromRange = transformRanges(range);
				pageURIs.addAll(Arrays.asList(pageUriFromRange));
			}
		}

		return pageURIs.toArray(new String[pageURIs.size()]);
	}

	private String[] splitRanges(String rangesParameter) {
		return rangesParameter.split(",");
	}

	private String[] transformRanges(String range) {
		if (PageIndexParser.isValid(range)) {
			return PageIndexParser.parse(range, pageUris);
		} 
//		else if (isPageName(range)) {
			// TODO: not implemented yet
			// return PageNameParser.parse(range, pageUris);
//		}

		return null;
	}


}
