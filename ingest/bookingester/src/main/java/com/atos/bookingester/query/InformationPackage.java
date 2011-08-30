package com.atos.bookingester.query;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.atos.bookingester.rebuild.FedoraObject;
import com.atos.bookingester.rebuild.FedoraObjectType;
import com.atos.bookingester.util.MetsReader;
import com.atos.bookingester.util.MyHttpClient;

public class InformationPackage {
	private String date;
	private String path;
	private String user;
	private String metsfile;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getMetsfile() {
		return metsfile;
	}

	public void setMetsfile(String metsfile) {
		this.metsfile = metsfile;
	}

	public InputStream getMetsfileAsStream() throws IOException{
		return MyHttpClient.getAsStream(metsfile);
	}
	
	@Override
	public String toString() {
		return "METS: " + this.metsfile;
	}

}
