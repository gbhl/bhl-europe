package com.bhle.access.bookreader;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import com.bhle.access.util.Olef;

public class BookInfo {
	private String guid;
	
	private String title;
	private URL url;
	private int entryPageIndex;
	private List<PageInfo> pages = new ArrayList<PageInfo>();

	private Olef olef;

	private TableOfContent toc;

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public int getEntryPageIndex() {
		return entryPageIndex;
	}

	public void setEntryPageIndex(int entryPageIndex) {
		this.entryPageIndex = entryPageIndex;
	}

	public List<PageInfo> getPages() {
		return pages;
	}

	public void setPages(List<PageInfo> pages) {
		this.pages = pages;
	}

	public void addPage(PageInfo page) {
		pages.add(page);
	}

	Olef getOlef() {
		return olef;
	}

	void setOlef(Olef olef) {
		this.olef = olef;
	}

}
