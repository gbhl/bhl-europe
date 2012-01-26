package com.bhle.access.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class WildcardUtil {
	public static File[] listFiles(File dir, String wildcardPath) {
		int asterisk = wildcardPath.indexOf("*");
		int question = wildcardPath.indexOf("?");
		asterisk = asterisk == -1 ? Integer.MAX_VALUE : asterisk;
		question = question == -1 ? Integer.MAX_VALUE : question;
		int i = Math.min(asterisk, question);
		int lastSeparator = wildcardPath.lastIndexOf("/", i -1);
		
		String tmpBaseDir = dir.getAbsolutePath();
		if (lastSeparator != -1){
			tmpBaseDir += "/" + wildcardPath.substring(0, lastSeparator);
		}
		
		String wildcardRegex = wildcardToRegex(wildcardPath);
		List<File> result = new ArrayList<File>();

		System.out.println("Base Dir: " + tmpBaseDir);
		System.out.println("Regex: " + wildcardRegex);
		Iterator<File> iterator = null;
		for (iterator = FileUtils.iterateFiles(new File(tmpBaseDir), null, true); iterator
				.hasNext();) {
			File file = iterator.next();
			System.out.println("luguo: " + file);
			if (Pattern.matches(wildcardRegex, file.toString())) {
				System.out.println(file);
				result.add(file);
			}
		}
		return result.toArray(new File[result.size()]);
	}

	public static String wildcardToRegex(String wildcard) {
		StringBuffer s = new StringBuffer(wildcard.length());
		s.append("^.*");
		for (int i = 0, is = wildcard.length(); i < is; i++) {
			char c = wildcard.charAt(i);
			switch (c) {
			case '*':
				s.append("[^\\\\/]*");
				break;
			case '?':
				s.append("[^\\\\/]");
				break;
			// Separator
			case '/':
				s.append("[\\\\/]");
				break;
			// escape special regexp-characters
			case '(':
			case ')':
			case '[':
			case ']':
			case '$':
			case '^':
			case '.':
			case '{':
			case '}':
			case '|':
			case '\\':
				s.append("\\");
				s.append(c);
				break;
			default:
				s.append(c);
				break;
			}
		}
		s.append('$');
		return (s.toString());
	}
}
