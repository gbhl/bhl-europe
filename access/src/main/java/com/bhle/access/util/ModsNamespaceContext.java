package com.bhle.access.util;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class ModsNamespaceContext implements NamespaceContext {

	public String getNamespaceURI(String prefix) {
		return "http://www.loc.gov/mods/v3";
	}

	public String getPrefix(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Iterator getPrefixes(String arg0) {
		throw new UnsupportedOperationException();
	}

}
