package com.bhle.access.bookreader.search;

import java.util.ArrayList;
import java.util.List;

public class Match {
	private String text;

	private List<Parameter> par = new ArrayList<Parameter>();

	public Match(StringWithBox pageText, String query) {
		int pageNumber = getPageNumberFromSequence(pageText.getSequence());

		Parameter para = new Parameter();
		para.setPage(pageNumber);
		para.setBoxes(pageText.generateBoxes(query, pageNumber));
		par.add(para);

		this.text = "{{{" + pageText.getQuery() + "}}}";

	}

	private int getPageNumberFromSequence(int sequence) {
		return sequence;
	}

	public String getText() {
		return text;
	}

	public List<Parameter> getPar() {
		return par;
	}

}
