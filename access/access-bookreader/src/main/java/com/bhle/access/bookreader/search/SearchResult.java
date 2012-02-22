package com.bhle.access.bookreader.search;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

public class SearchResult {
	private List<Match> matches = new ArrayList<Match>();

	public void add(StringWithBox pageText, String query) {
		Match match = new Match(pageText, query);
		matches.add(match);
	}

	public List<Match> getMatches() {
		return matches;
	}

	public String toJSON() {
		JSONObject jsonObject = JSONObject.fromObject(this);
		return jsonObject.toString();
	}

}
