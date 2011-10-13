package com.atos.bookingester.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.atos.bookingester.rebuild.FedoraObjectType;

public class MetsReader extends DefaultHandler {

	private FedoraObjectType type;

	private boolean startDatastreams = false;

	public FedoraObjectType getFedoraObjectType() {
		return type;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes a) throws SAXException {
		if(qName.equalsIgnoreCase("fileGrp") && a.getValue("ID").equalsIgnoreCase("DATASTREAMS")){
			startDatastreams = true;
		}
		
		if(startDatastreams && qName.equalsIgnoreCase("fileGrp") && a.getValue("ID").startsWith("INFORMATIONPACKAGE")){
			System.out.println("BOOK_COLLECTION");
			type = FedoraObjectType.BOOK_COLLECTION;
			startDatastreams = false;
		}
		
		if(startDatastreams && qName.equalsIgnoreCase("fileGrp") && a.getValue("ID").startsWith("IMAGE")){
			System.out.println("BOOK");
			type = FedoraObjectType.BOOK;
			startDatastreams = false;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
	}

}
