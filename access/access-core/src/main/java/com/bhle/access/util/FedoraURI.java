package com.bhle.access.util;

import java.net.URI;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.akubraproject.UnsupportedIdException;
import org.springframework.beans.factory.annotation.Value;

public class FedoraURI {

	public static String GUID_BANK_ID;

	public void setGuidBankId(String bankId) {
		FedoraURI.GUID_BANK_ID = bankId;
	}

	public static String DEFAULT_NAMESPACE;

	public void setDefaultNamespace(String namespace) {
		FedoraURI.DEFAULT_NAMESPACE = namespace;
	}

	public static String DEFAULT_SCHEME = "info";

	private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("00000");

	private URI uri;

	private String scheme;
	private String pid;
	private String namespace;
	private String guid;
	private String dsid;
	private String serialNumber;

	public FedoraURI() {
	}

	public FedoraURI(URI uri) {
		this.uri = uri;

		this.scheme = uri.getScheme();
		String path = uri.getSchemeSpecificPart();
		Pattern pattern = Pattern
				.compile("^fedora/((\\w+):(?:[^-/]+)-([^-/]+)-?(\\d*))/?(\\w*)$");
		Matcher matcher = pattern.matcher(path);
		if (matcher.find()) {
			pid = matcher.group(1);
			namespace = matcher.group(2);
			guid = matcher.group(3);
			serialNumber = matcher.group(4);
			dsid = matcher.group(5);
		} else {
			try {
				throw new UnsupportedIdException(uri);
			} catch (UnsupportedIdException e) {
				e.printStackTrace();
			}
		}
	}

	public String getPid() {
		return pid;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getGuid() {
		return guid;
	}

	public String getDsid() {
		return dsid;
	}

	public String getScheme() {
		return scheme;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public URI toURI() {
		return uri;
	}

	public URI getItemURI() {
		return URI.create(scheme + ":fedora/" + pid);
	}

	public static String getPidFromGuid(String guid) {
		return FedoraURI.DEFAULT_NAMESPACE + ":" + GUID_BANK_ID + "-" + guid;
	}

	public static FedoraURI getFedoraUri(String guid, String dsid) {
		return new FedoraURI(URI.create(DEFAULT_SCHEME + ":fedora/"
				+ getPidFromGuid(guid) + "/" + dsid));
	}

	public static FedoraURI getFedoraUri(String guid, int index, String dsid) {
		String serialNumber = DECIMAL_FORMAT.format(index);
		return new FedoraURI(URI.create(DEFAULT_SCHEME + ":fedora/"
				+ getPidFromGuid(guid) + "-" + serialNumber + "/" + dsid));
	}

}
