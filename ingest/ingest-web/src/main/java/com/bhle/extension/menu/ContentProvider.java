package com.bhle.extension.menu;

public interface ContentProvider {
	boolean openFile();
	String getContent();
	//long getNewestFileSize();
	void setOldFileSize(long oldFileSeze);

}
