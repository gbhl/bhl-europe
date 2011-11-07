package util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SerialPagePIDExtractor implements PagePIDExtractor {

	private int pageCount;
	private String pid;

	public List<String> getPIDs(String pid, String requestParameter) {
		this.pid = pid;
		pageCount = ResourceIndex.getPageCount(pid);

		List<Range> ranges = splitRanges(requestParameter);

		fixRangesConsistency(ranges);

		List<String> pids = mergeRanges(ranges);

		return pids;
	}

	private List<Range> splitRanges(String requestParameter) {
		List<Range> result = new ArrayList<Range>();

		if (requestParameter == null || requestParameter.trim().equals("")) {
			result.add(new Range(1, pageCount));
		} else {
			String[] rangesArray = requestParameter.split(",");
			for (String rangeString : rangesArray) {
				result.add(new Range(rangeString));
			}
		}
		return result;
	}

	private void fixRangesConsistency(List<Range> ranges) {
		for (Range range : ranges) {
			if (range.getLower() > range.getUpper()) {
				switchLowerAndUpper(range);
			}

			if (range.getUpper() > pageCount) {
				range.setUpper(pageCount);
			}
			
			if (range.getUpper() > pageCount && range.getLower() > pageCount) {
				ranges.remove(range);
			}
		}
	}

	private List<String> mergeRanges(List<Range> ranges) {
		DecimalFormat formatter = new DecimalFormat("0000");

		List<String> result = new ArrayList<String>();

		for (Range range : ranges) {
			for (int i = range.getLower(); i <= range.getUpper(); i++) {
				result.add(pid + "-" + formatter.format(i));
			}
		}
		return result;
	}

	private void switchLowerAndUpper(Range range) {
		int temp;
		temp = range.getLower();
		range.setLower(range.getUpper());
		range.setUpper(temp);
	}
}
