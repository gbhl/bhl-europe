package com.bhle.access.oai;

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
import proai.driver.impl.RemoteIteratorImpl;
import proai.error.RepositoryException;

public class OAIDriverImpl implements OAIDriver {

	private RecordProvider recordProvider;
	private SetInfoPrivoder setInfoPrivoder;
	private MetadataFormatProvider metadataFormatProvider;

	public OAIDriverImpl() {
	}

	public void init(Properties props) throws RepositoryException {
		setInfoPrivoder = new SetInfoProviderImpl(props);
		metadataFormatProvider = new MetadataFormatProviderImpl(props);
		recordProvider = new RecordProviderImpl(setInfoPrivoder,
				metadataFormatProvider);
	}

	public void close() throws RepositoryException {
		// do nothing (this impl doesn't tie up any resources)
	}

	public Date getLatestDate() {
		return new Date();
	}

	public RemoteIterator<MetadataFormat> listMetadataFormats()
			throws RepositoryException {
		return new RemoteIteratorImpl<MetadataFormat>(metadataFormatProvider
				.getMetadataFormatCollection().iterator());
	}

	public RemoteIterator<Record> listRecords(Date from, Date until,
			String mdPrefix) throws RepositoryException {
		return new RemoteIteratorImpl<Record>(recordProvider
				.getRecordCollection(from, until, mdPrefix).iterator());
	}

	public RemoteIterator<SetInfo> listSetInfo() throws RepositoryException {
		return new RemoteIteratorImpl<SetInfo>(setInfoPrivoder
				.getSetInfoCollection().iterator());
	}

	public void write(PrintWriter out) throws RepositoryException {
		try {
			IOUtils.copy(this.getClass().getResourceAsStream("/identity.xml"),
					out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void writeRecordXML(String itemID, String mdPrefix,
			String sourceInfo, PrintWriter writer) throws RepositoryException {
		StringBuffer xml = new StringBuffer();
		SourceInfo info = new SourceInfo(sourceInfo);
		xml.append("<record>");
		xml.append(getRecordHeader(itemID, info));
		xml.append(getRecordMetadata(mdPrefix, info));
		xml.append("</record>");
		System.out.println(xml.toString());
		writer.write(xml.toString());
	}

	private String getRecordHeader(String itemID, SourceInfo sourceInfo) {
		StringBuffer xml = new StringBuffer();
		xml.append("<header>");
		xml.append("<identifier>" + itemID + "</identifier>");
		xml.append("<datestamp>" + sourceInfo.getDatestamp() + "</datestamp>");
		xml.append("<setSpec>" + sourceInfo.getSetSpec() + "</setSpec>");
		xml.append("</header>");
		return xml.toString();
	}

	private Object getRecordMetadata(String mdPrefix, SourceInfo sourceInfo) {
		StringBuffer xml = new StringBuffer();
		try {
			xml.append("<metadata>"
					+ IOUtils.toString(sourceInfo.getUrl().openStream()).replaceAll("<\\?xml.*\\?>", "")
					+ "</metadata>");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return xml.toString();
	}
}
