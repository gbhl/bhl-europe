package com.bhle.access.bookreader.search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.storage.StorageService;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.StaticURI;

public class SearchService {

	private static final Logger logger = LoggerFactory
			.getLogger(SearchService.class);

	private static StorageService storageService;

	public void setStorageService(StorageService storageService) {
		SearchService.storageService = storageService;
	}

	public static String query(String guid, String queries) {
		String[] queriesArray = queries.split(" ");

		SearchResult result = new SearchResult();
		for (StringWithBox pageText : getPageTexts(guid)) {
			for (String query : queriesArray) {
				if (pageText.contains(query)) {
					// pageText.populateWithBox();
					result.add(pageText, query);
				}
			}
		}
		return result.toJSON();
	}

	private static List<StringWithBox> getPageTexts(String guid) {
		// String[] pageUris = FedoraUtil.getAllMembers(
		// FedoraURI.getPidFromGuid(guid), FedoraUtil.PAGE_MODEL);

		List<URI> dsUris = null;
		try {
			dsUris = storageService.getDatastream(guid, "OCR");
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<StringWithBox> result = new ArrayList<StringWithBox>();

		for (URI dsUri : dsUris) {
			FedoraURI pageFedoraUri = new FedoraURI(dsUri);
			URI pageHttpUri = StaticURI.toStaticFileUri(pageFedoraUri);
			String text = "";
			try {
				text = IOUtils.toString(pageHttpUri.toURL().openStream());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			int sequence = Integer.valueOf(pageFedoraUri.getSerialNumber());
			result.add(new StringWithBox(sequence, text));
		}
		return result;
	}
}
