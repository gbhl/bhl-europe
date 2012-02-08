package com.bhle.access.storage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Pattern pattern = Pattern
				.compile("^fedora/((\\w+):(?:[^-/]+)-([^-/]+)-?(\\d*))/?(\\w*)$");
		Matcher matcher = pattern
				.matcher("fedora/bhle:10706-a00000000000132805961115/JP2");
		if (matcher.find()) {
			for (int i = 1; i <= 5; i++) {
				System.out.println(matcher.group(i));
			}
		}

	}

}
