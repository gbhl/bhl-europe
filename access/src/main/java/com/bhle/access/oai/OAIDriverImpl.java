package com.bhle.access.oai;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import proai.MetadataFormat;
import proai.Record;
import proai.SetInfo;
import proai.driver.OAIDriver;
import proai.driver.RemoteIterator;
import proai.error.RepositoryException;

public class OAIDriverImpl implements OAIDriver {

	public void close() throws RepositoryException {
	}

	public Date getLatestDate() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public void init(Properties properties) throws RepositoryException {
		// TODO Auto-generated method stub

	}

	public RemoteIterator<? extends MetadataFormat> listMetadataFormats()
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public RemoteIterator<? extends Record> listRecords(Date arg0, Date arg1,
			String arg2) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public RemoteIterator<? extends SetInfo> listSetInfo()
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public void write(PrintWriter arg0) throws RepositoryException {
		// TODO Auto-generated method stub

	}

	public void writeRecordXML(String itemID, String mdPrefix,
			String sourceInfo, PrintWriter writer) throws RepositoryException {
		try {
			FileInputStream fi = new FileInputStream(sourceInfo);
			IOUtils.copy(fi, writer);
			IOUtils.closeQuietly(fi);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
