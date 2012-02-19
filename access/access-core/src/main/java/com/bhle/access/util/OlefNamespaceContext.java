package com.bhle.access.util;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

public class OlefNamespaceContext implements NamespaceContext {

	public String getNamespaceURI(String prefix) {
		if(prefix.equals("mods")){
			return "http://www.loc.gov/mods/v3";
		} else if (prefix.equals("dwc")){
			return "http://rs.tdwg.org/dwc/terms/";
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
