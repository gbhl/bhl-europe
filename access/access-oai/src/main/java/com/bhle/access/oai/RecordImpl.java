package com.bhle.access.oai;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import proai.Record;

public class RecordImpl implements Record {

	private String itemID;
	private String prefix;
	private String sourceInfo;

	public RecordImpl(String itemID, String prefix, Date date, String setSpec,
			URL url) {
		super();
		this.itemID = itemID;
		this.prefix = prefix;
		SourceInfo info = new SourceInfo();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		info.setDatestamp(df.format(date));
		info.setSetSpec(setSpec);
		info.setUrl(url);
		this.sourceInfo = info.toString();
	}

	@Override
	public String getItemID() {
		return itemID;
	}

	@Override
	public String getPrefix() {
		return prefix;
	}

	@Override
	public String getSourceInfo() {
		return sourceInfo;
	}

	@Override
	public String toString() {
		return itemID + " " + prefix + " {" + sourceInfo + "}";
	}
}
