package com.atosorigin.bhle;

import java.net.URI;

public class Test {

	public static void main(String[] args) {
		URI uri  = URI.create("info:fedora/demo:aaa/AAA/AAA.0");
		String dsid = "AAA";
		String[] parts = uri.getSchemeSpecificPart().split("/");
		System.out.println(parts[parts.length - 2]);
		

	}

}
