package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import resource.FedoraObjectService;

import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import exception.BoxInvalidException;

public class StringWithBox extends HashMap<Integer, Box> {
	private static final long serialVersionUID = -1099878838111370667L;

	private String text;
	private String query;
	private String pid;

	private boolean hasBoxes = false;

	public StringWithBox(String pid, String text) {
		this.pid = pid;
		this.text = text;
	}

	public boolean contains(String query) {
		Pattern p = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(text);
		
		if (m.find()){
			this.query = text.substring(m.start(), m.end()); 
			return true;
		} else {
			return false;
		}
	}

	public boolean hasBoxes() {
		return hasBoxes;
	}

	public void populateWithBox() {
		List<Box> boxes;
		try {
			boxes = getTextBoxes(pid);
		} catch (BoxInvalidException e) {
			//e.printStackTrace();
			hasBoxes = false;
			return;
		}
		hasBoxes = true;

		List<String> boxCharList = new ArrayList<String>();
		for (Box box : boxes) {
			boxCharList.add(box.getText());
		}

		List<String> textList = splitStringByChar(text);
		Patch patch = DiffUtils.diff(textList, boxCharList);

		for (Delta delta : patch.getDeltas()) {
			// System.out.println(delta.getOriginal().getPosition());
			// System.out.println(delta.getRevised().getPosition());
			// System.out.println(delta.getOriginal().getLines());
			// System.out.println(delta.getRevised().getLines());
			int revisedposition = delta.getRevised().getPosition();
			int originalposition = delta.getOriginal().getPosition();
			Box lastBox = boxes.get(revisedposition - 1 < 0 ? 0
					: revisedposition - 1);
			List<String> insertions = (List<String>) delta.getOriginal()
					.getLines();
			switch (delta.getType()) {
			case INSERT:
				boxes.remove(revisedposition);
				break;
			case CHANGE:
				boxes.remove(revisedposition);
				for (String insertion : insertions) {
					// create a empty box with the extra text from OCR
					boxes.add(
							originalposition,
							new Box(insertion, lastBox.getLeft(), lastBox
									.getBottom(), lastBox.getLeft(), lastBox
									.getBottom()));
				}
				break;
			case DELETE:
				for (String insertion : insertions) {
					// create a empty box with the extra text from OCR
					boxes.add(
							originalposition,
							new Box(insertion, lastBox.getLeft(), lastBox
									.getBottom(), lastBox.getLeft(), lastBox
									.getBottom()));
				}
				break;
			default:
				break;
			}
		}

		for (int i = 0; i < boxes.size(); i++) {
			this.put(i, boxes.get(i));
		}
	}

	private List<String> splitStringByChar(String text) {
		List<String> result = new ArrayList<String>();

		char[] chars = text.toCharArray();
		for (char character : chars) {
			result.add(String.valueOf(character));
		}
		return result;
	}

	private List<Box> getTextBoxes(String pid) throws BoxInvalidException {
		List<Box> boxes = new ArrayList<Box>();

		try {
			BufferedReader br = getTextBoxesReader(pid);
			String line = null;
			while ((line = br.readLine()) != null) {
				boxes.add(new Box(line));
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return boxes;
	}

	private BufferedReader getTextBoxesReader(String pid)
			throws BoxInvalidException {
		BufferedReader br = null;

		try {
			URL url = new URL(FedoraObjectService.getBoxURLFromPID(pid));
			br = new BufferedReader(new InputStreamReader(url.openStream()));

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
			throw new BoxInvalidException();
		}

		return br;
	}

	public Box getBox(int index) {
		return this.get(index);
	}

	public String getQuery() {
		return query;
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

	public String getPid() {
		return pid;
	}

}
