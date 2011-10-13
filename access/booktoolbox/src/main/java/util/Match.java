package util;

import java.util.ArrayList;
import java.util.List;

public class Match {
	private String text;
//	private List<Box> boxes = new ArrayList<Box>();
//	private int pageNumber;
	
	private List<Parameter> par = new ArrayList<Parameter>();
	
	public Match(StringWithBox pageText, String query) {
		int pageNumber = getPageNumberFromPid(pageText.getPid());
		
		Parameter para = new Parameter();
		para.setPage(pageNumber);
		para.setBoxes(pageText.generateBoxes(query, pageNumber));
		par.add(para);
		
		this.text = "{{{" + pageText.getQuery() + "}}}";
		
	}
	
	private int getPageNumberFromPid(String pid) {
		String pageNumber = pid.substring(pid.indexOf("-") + 1);
		return Integer.valueOf(pageNumber);
	}

	public String getText() {
		return text;
	}

	public List<Parameter> getPar() {
		return par;
	}
	
	
}
