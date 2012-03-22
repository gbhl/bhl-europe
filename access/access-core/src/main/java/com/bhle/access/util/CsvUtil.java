package com.bhle.access.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.csvreader.CsvReader;

public class CsvUtil {
	public static String[] readOneColumnCsv(String csv) {
		List<Map<String, String>> csvList = read(csv);
		List<String> result = new ArrayList<String>();
		for (Map<String, String> map : csvList) {
			Set<String> keySet = map.keySet();
			if (keySet.size() > 1) {
				throw new IllegalArgumentException(
						"CSV has more than one column");
			}
			for (String key : keySet) {
				result.add(map.get(key));
				break;
			}
		}
		return result.toArray(new String[] {});
	}

	public static List<Map<String, String>> read(String csv) {
		List<Map<String, String>> csvList = new ArrayList<Map<String, String>>();

		CsvReader csvReader;
		try {
			csvReader = new CsvReader(new StringReader(csv));
			if (csvReader.readHeaders()) {
				String[] headers = csvReader.getHeaders();
				while (csvReader.readRecord()) {
					Map<String, String> csvMap = new HashMap<String, String>();
					for (String head : headers) {
						csvMap.put(head, csvReader.get(head));
					}
					csvList.add(csvMap);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return csvList;
	}
}
