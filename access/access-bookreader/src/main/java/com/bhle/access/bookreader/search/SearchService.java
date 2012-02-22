package com.bhle.access.bookreader.search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.FedoraUtil;
import com.bhle.access.util.StaticURI;

public class SearchService {
	public static String query(String guid, String queries) {
		String[] queriesArray = queries.split(" ");

		SearchResult result = new SearchResult();
		for (StringWithBox pageText : getPageTexts(guid)) {
			for (String query : queriesArray) {
				if (pageText.contains(query)) {
//					pageText.populateWithBox();
					result.add(pageText, query);
				}
			}
		}
		return result.toJSON();
	}

	private static List<StringWithBox> getPageTexts(String guid) {
		String[] pageUris = FedoraUtil.getAllMembers(
				FedoraURI.getPidFromGuid(guid), FedoraUtil.PAGE_MODEL);

		List<StringWithBox> result = new ArrayList<StringWithBox>();
		
		for (String pageUri : pageUris) {
			FedoraURI pageFedoraUri = new FedoraURI(URI.create(pageUri + "/OCR"));
			URI pageHttpUri = StaticURI.toStaticHttpUri(pageFedoraUri);
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
