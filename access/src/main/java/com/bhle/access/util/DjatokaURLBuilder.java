package com.bhle.access.util;

import java.net.MalformedURLException;
import java.net.URL;

public class DjatokaURLBuilder {
	private static String BASE_URL;

	private static final String URL_VER = "url_ver";
	private static final String RFT_ID = "rft_id";
	private static final String SVC_ID = "svc_id";
	private static final String SVC_FORMAT = "svc.format";
	private static final String SVC_LEVEL = "svc.level";

	private static final int DEFAULT_LEVEL = 3;

	public void setBaseUrl(String baseUrl) {
		BASE_URL = baseUrl;
	}

	public static URL build(URL referent) {
		return build(referent, DEFAULT_LEVEL);
	}
	
	public static URL build(URL referent, int level) {
		URL url = null;
		try {
			url = new URL(BASE_URL + "resolver?" + getUrlVersion() + "&"
					+ getRefId(referent) + "&" + getSvcId() + "&"
					+ getSvcFormat() + "&" + getSvcLevel(level));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return url;
	}

	private static String getUrlVersion() {
		return getParameter(URL_VER, "Z39.88-2004");
	}

	private static String getSvcLevel(int level) {
		return getParameter(SVC_LEVEL, String.valueOf(level));
	}

	private static String getSvcFormat() {
		return getParameter(SVC_FORMAT, "image/jpeg");
	}

	private static String getSvcId() {
		return getParameter(SVC_ID, "info:lanl-repo/svc/getRegion");
	}

	private static String getRefId(URL referent) {
		return getParameter(RFT_ID, referent.toString());
	}

	private static String getParameter(String key, String value) {
		return key + "=" + value;
	}
}
