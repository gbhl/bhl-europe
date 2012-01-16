package com.bhle.access.aop;

import java.util.regex.Pattern;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String regex = "/(batch-admin|images|script|style|(WEB-INF/jsp))/.*";
		Pattern p = Pattern.compile(regex);
		System.out.println(p.matcher("/batch-admin/home").matches());

	}

}
