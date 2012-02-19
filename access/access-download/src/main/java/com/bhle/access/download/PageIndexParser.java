package com.bhle.access.download;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.util.FedoraURI;

public class PageIndexParser {

	private static final Logger logger = LoggerFactory
			.getLogger(PageIndexParser.class);

	private static String INDEX_REGEX = "n\\d+|n\\d+-n\\d+";

	private static DecimalFormat PAGE_INDEX_FORMATTER = new DecimalFormat(
			"00000");

	public static String[] parse(String range, String[] pageUris) {
		List<String> result = new ArrayList<String>();

		if (range.contains("-")) {

			logger.info("Parge Index Range: " + range);

			String[] limits = range.split("-");
			String lowerBound = limits[0];
			String higherBound = limits[1];

			int lowPageIndex = Integer.valueOf(lowerBound.substring(1));
			String lowerSerialNumber = PAGE_INDEX_FORMATTER
					.format(lowPageIndex);

			int highPageIndex = Integer.valueOf(higherBound.substring(1));
			String higherSerialNumber = PAGE_INDEX_FORMATTER
					.format(highPageIndex);

			String smallestSerialNumber = getFirstSerialNumber(pageUris);
			String biggestSerialNumber = getLastSerialNumber(pageUris);

			int from = lowerSerialNumber.compareTo(smallestSerialNumber) < 0 ? 0
					: Integer.MAX_VALUE;
			int to = higherSerialNumber.compareTo(biggestSerialNumber) > 0 ? pageUris.length - 1
					: Integer.MIN_VALUE;

			for (int i = 0; i < pageUris.length; i++) {
				FedoraURI fedoraUri = new FedoraURI(URI.create(pageUris[i]));
				logger.info(fedoraUri.getSerialNumber());
				if (lowerSerialNumber.equals(fedoraUri.getSerialNumber())) {
					from = i;
				}

				if (higherSerialNumber.equals(fedoraUri.getSerialNumber())) {
					to = i;
				}
			}

			logger.info("from: " + from);
			logger.info("to: " + to);

			return Arrays.copyOfRange(pageUris, from, to + 1);
		} else {

			logger.info("Parge Single Index: " + range);

			// remove "n"
			int pageIndex = Integer.valueOf(range.substring(1));
			String serialNumber = PAGE_INDEX_FORMATTER.format(pageIndex);

			for (String pageUri : pageUris) {
				FedoraURI fedoraUri = new FedoraURI(URI.create(pageUri));
				if (serialNumber.equals(fedoraUri.getSerialNumber())) {
					result.add(pageUri);
					break;
				}
			}
		}
		return result.toArray(new String[result.size()]);
	}

	private static String getFirstSerialNumber(String[] pageUris) {
		FedoraURI fedoraUri = new FedoraURI(URI.create(pageUris[0]));
		return fedoraUri.getSerialNumber();
	}

	private static String getLastSerialNumber(String[] pageUris) {
		FedoraURI fedoraUri = new FedoraURI(
				URI.create(pageUris[pageUris.length - 1]));
		return fedoraUri.getSerialNumber();
	}

	public static boolean isValid(String range) {
		return Pattern.matches(INDEX_REGEX, range);
	}

}
