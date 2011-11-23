package util;

import java.util.ArrayList;
import java.util.List;

public class FileNameExtractor {
	public static String abstractName(List<String> pids) {
		List<String> fileNameList = new ArrayList<String>();
		for (String pid : pids) {
			String parentPid = pid.split("-")[0];
			if (!fileNameList.contains(parentPid)) {
				fileNameList.add(parentPid);
			}
		}

		String totalName = "";
		for (String name : fileNameList) {
			totalName += name + "&";
		}
		return totalName.substring(0, totalName.length() - 1);
	}
}
