package com.bhle.access.bookreader.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringWithBox extends HashMap<Integer, Box> {
	private static final Logger logger = LoggerFactory
			.getLogger(StringWithBox.class);

	private static final long serialVersionUID = 1L;

	private String text;
	private String query;
	private int sequence;

	private boolean hasBoxes = false;

	public StringWithBox(int sequence, String text) {
		this.sequence = sequence;
		this.text = text;
	}

	public int getSequence() {
		return sequence;
	}

	public String getQuery() {
		return query;
	}

	public boolean contains(String query) {
		Pattern p = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(text);

		if (m.find()) {
			this.query = text.substring(m.start(), m.end());
			logger.debug("Find query {} in {}", new String[] { query, text });
			return true;
		} else {
			return false;
		}
	}

	public boolean hasBoxes() {
		return hasBoxes;
	}

	public Box getBox(int index) {
		return this.get(index);
	}

	public List<Box> generateBoxes(String query, int page) {
		List<Box> result = new ArrayList<Box>();

		if (hasBoxes) {
			Pattern p = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(text);
			while (m.find()) {
				List<Box> boxArray = new ArrayList<Box>();
				for (int i = m.start(); i < m.end(); i++) {
					boxArray.add(this.get(i));
				}
				Box resultBox = Box.merge(boxArray.toArray(new Box[0]));
				resultBox.setPage(page);
				result.add(resultBox);
			}
		}
		return result;
	}
}
