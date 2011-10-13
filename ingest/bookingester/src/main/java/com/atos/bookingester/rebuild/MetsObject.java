package com.atos.bookingester.rebuild;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import org.apache.commons.lang.RandomStringUtils;
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

	private InformationPackage pack;
	private METS mets;

	public MetsObject(InformationPackage pack) {
		this.pack = pack;

		try {
			METSReader mr = new METSReader();
			mr.mapToDOM(getMetsInputStream(pack));
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
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private InputStream getMetsInputStream(InformationPackage pack)
			throws Exception {
		switch (pack.getType()) {
		case URL:
			return MyHttpClient.getAsStream(pack.getMetsfile());
		case LOCAL:
			return new FileInputStream(pack.getMetsfile());
		default:
			throw new Exception("The type of METS file source is unsupported");
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
		StringBuffer xml = new StringBuffer();

		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					getMetsInputStream(pack)));
			String line = "";
			while ((line = br.readLine()) != null) {
				xml.append(line);
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		String mods = xml.substring(xml.indexOf("<bibliographicInformation>")
				+ "<bibliographicInformation>".length(),
				xml.indexOf("</bibliographicInformation>"));
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

	public String getID() {
		Pattern regex = Pattern.compile("(?<=\\sID:\\s\\d{5}+/)\\w+");
		Matcher m = regex.matcher(mets.getLabel());
		if (m.find()) {
			return m.group();
		} else {
			return "NOID" + RandomStringUtils.random(5);
		}
	}

}
