package com.atos.bookingester.rebuild;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import au.edu.apsr.mtk.base.FileGrp;
import au.edu.apsr.mtk.base.METS;
import au.edu.apsr.mtk.base.METSException;
import au.edu.apsr.mtk.base.METSWrapper;
import au.edu.apsr.mtk.ch.METSReader;

import com.atos.bookingester.query.InformationPackage;
import com.atos.bookingester.util.MyHttpClient;

public class MetsObject {

	private METS mets;
	private String location;

	public MetsObject(InformationPackage pack) {
		InputStream in;
		try {
			location = pack.getMetsfile();
			in = MyHttpClient.getAsStream(location);
			METSReader mr = new METSReader();
			mr.mapToDOM(in);
			METSWrapper mw = new METSWrapper(mr.getMETSDocument());
			mets = mw.getMETSObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (METSException e) {
			e.printStackTrace();
		}

	}

	public FedoraObjectType getType() {
		for (FileGrp fileGrpInner : getDatastreamFileGrps()) {
			if (fileGrpInner.getID().startsWith("INFORMATIONPACKAGE")) {
				return FedoraObjectType.BOOK_COLLECTION;
			}
		}

		return FedoraObjectType.BOOK;
	}

	public String getMODS() {
		String xml = MyHttpClient.getAsString(location);
		String mods = xml.substring(xml.indexOf("<bibliographicInformation>") + "<bibliographicInformation>".length(), xml.indexOf("</bibliographicInformation>"));
		mods = "<mods xmlns=\"http://www.loc.gov/mods/v3\">" + mods + "</mods>";
		return mods.trim();
	}

	public List<FileGrp> getDatastreamFileGrps() {
		try {
			for (FileGrp fileGrp : mets.getFileSec().getFileGrps()) {
				if (fileGrp.getID().equals("DATASTREAMS")) {
					return fileGrp.getFileGrps();
				}
			}
		} catch (METSException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getID(){
		Pattern regex = Pattern.compile("(?<=\\sID:\\s\\d{5}+/)\\w+");
		Matcher m = regex.matcher(mets.getLabel());
		if(m.find()){
			return m.group();
		} else {
			return "NOID";
		}
	}

}
