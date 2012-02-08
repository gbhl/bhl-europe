package com.bhle.access.bookreader;

import java.util.ArrayList;
import java.util.List;

public class PageInfo implements Comparable<PageInfo>{
	private String name;
	private int index;
	
	private int width;
	private int height;
	
	private String url;
	
	private String ocrUrl;
	
	private List<String> scientificNames = new ArrayList<String>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int compareTo(PageInfo page) {
		return this.index - page.index;
	}

	public List<String> getScientificNames() {
		return scientificNames;
	}

	public void setScientificNames(List<String> scientificNames) {
		this.scientificNames = scientificNames;
	}

	public String getOcrUrl() {
		return ocrUrl;
	}

	public void setOcrUrl(String ocrUrl) {
		this.ocrUrl = ocrUrl;
	}
}
