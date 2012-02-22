package com.bhle.access.bookreader.search;

import java.util.List;

public class Parameter {
	private List<Box> boxes;
	private int page;

	public List<Box> getBoxes() {
		return boxes;
	}

	public void setBoxes(List<Box> boxes) {
		this.boxes = boxes;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
}
