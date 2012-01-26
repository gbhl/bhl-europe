package com.bhle.access.util;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FedoraURI {

	public static String DEFAULT_NAMESPACE;

	public void setDefaultNamespace(String namespace) {
		FedoraURI.DEFAULT_NAMESPACE = namespace;
	}

	public static String DEFAULT_SCHEME = "info";

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
		Pattern datastreamPattern = Pattern.compile("fedora/.*:.*/.*");
		Matcher datastreamMatcher = datastreamPattern.matcher(path);

		Pattern objectPattern = Pattern.compile("fedora/.*:[^/]*");
		Matcher objectMatcher = objectPattern.matcher(path);

		if (datastreamMatcher.matches()) {
			String[] frags = path.split("/");
			this.pid = frags[1];
			this.namespace = pid.substring(0, pid.indexOf(":"));
			this.dsid = frags[2];
		} else if (objectMatcher.matches()) {
			String[] frags = path.split("/");
			this.pid = frags[1];
		} else {
			throw new UnsupportedOperationException();
		}
		if (pid.contains("-")) {
			this.guid = pid.substring(pid.indexOf(":") + 1, pid.indexOf("-"));
			this.serialNumber = pid.substring(pid.indexOf("-") + 1);
		} else {
			this.guid = pid.substring(pid.indexOf(":") + 1);
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
		return FedoraURI.DEFAULT_NAMESPACE + ":" + guid;
	}

	public static FedoraURI getFedoraUri(String guid, String dsid) {
		return new FedoraURI(URI.create(DEFAULT_SCHEME + ":fedora/"
				+ getPidFromGuid(guid) + "/" + dsid));
	}

}
