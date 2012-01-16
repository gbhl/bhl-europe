package com.bhle.access.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;

public class CsvUtil {
	public static String[] readOneColumnCsv(String csv) {
		List<String> result = new ArrayList<String>();

		CsvReader csvReader;
		try {
			csvReader = new CsvReader(new StringReader(csv));
			csvReader.readHeaders();
			while (csvReader.readRecord()) {
				result.add(csvReader.getValues()[0]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toArray(new String[result.size()]);
	}
}
