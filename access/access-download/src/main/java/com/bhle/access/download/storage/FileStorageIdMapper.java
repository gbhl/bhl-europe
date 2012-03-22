package com.bhle.access.download.storage;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.akubraproject.map.IdMapper;

public class FileStorageIdMapper implements IdMapper {

	private static final String internalScheme = "file";
	private static final String externalScheme = "mailto";

	public URI getExternalId(URI internalId) throws NullPointerException {
		String path = internalId.getSchemeSpecificPart();
		String[] parts = path.split("/");
		String email = parts[0];
		String fileName = parts[1];
		return URI.create(externalScheme + ":" + decode(email) + "/"
				+ decode(fileName));
	}

	public URI getInternalId(URI externalId) throws NullPointerException {
		String path = externalId.getSchemeSpecificPart();
		String[] parts = path.split("/");
		String dir = parts[0];
		String fileName = parts[1];
		return URI.create(internalScheme + ":" + encode(dir) + "/"
				+ encode(fileName));
	}

	private String encode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}
	
	private String decode(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s;
	}

	public String getInternalPrefix(String externalPrefix)
			throws NullPointerException {
		return null;
	}

}
