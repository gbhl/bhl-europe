package com.bhle.access.oai;

import java.net.MalformedURLException;
import java.net.URL;

public class SourceInfo {
	private String datestamp;
	private String setSpec;
	private URL url;

	public SourceInfo(String sourceInfo) {
		String[] infos = sourceInfo.split(" ");
		datestamp = infos[0];
		setSpec = infos[1];
		try {
			url = new URL(infos[2]);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public SourceInfo() {
	}

	public String getDatestamp() {
		return datestamp;
	}

	public void setDatestamp(String datestamp) {
		this.datestamp = datestamp;
	}

	public String getSetSpec() {
		return setSpec;
	}

	public void setSetSpec(String setSpec) {
		this.setSpec = setSpec;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return datestamp + " " + setSpec + " " + url.toString();
	}
}
