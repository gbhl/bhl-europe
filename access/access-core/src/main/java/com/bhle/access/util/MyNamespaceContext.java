package com.bhle.access.util;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class MyNamespaceContext implements NamespaceContext {

	public String getNamespaceURI(String prefix) {
		if (prefix.equals("mods")) {
			return "http://www.loc.gov/mods/v3";
		} else if (prefix.equals("dwc")) {
			return "http://rs.tdwg.org/dwc/terms/";
		} else if (prefix.equals("olef")) {
			return "http://www.bhl-europe.eu/bhl-schema/v0.3/";
		} else if (prefix.equals("oai_dc")) {
			return "http://www.openarchives.org/OAI/2.0/oai_dc/";
		} else if (prefix.equals("dc")) {
			return "http://purl.org/dc/elements/1.1/";
		}
		return null;
	}

	public String getPrefix(String arg0) {
		throw new UnsupportedOperationException();
	}

	public Iterator getPrefixes(String arg0) {
		throw new UnsupportedOperationException();
	}

}
