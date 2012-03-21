package com.bhle.ingest.web;

public interface ContentProvider {
	boolean openFile();
	String getContent();
	//long getNewestFileSize();
	void setOldFileSize(long oldFileSeze);

}
