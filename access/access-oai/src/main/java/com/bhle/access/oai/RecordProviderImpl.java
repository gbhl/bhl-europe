package com.bhle.access.oai;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import proai.MetadataFormat;
import proai.Record;
import proai.SetInfo;
import proai.error.NoMetadataFormatsException;

import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.FedoraUtil;
import com.bhle.access.util.StaticURI;

public class RecordProviderImpl implements RecordProvider {

	private SetInfoPrivoder setInfoPrivoder;

	private MetadataFormatProvider metadataFormatProvider;

	public RecordProviderImpl(SetInfoPrivoder setInfoPrivoder,
			MetadataFormatProvider metadataFormatProvider) {
		super();
		this.setInfoPrivoder = setInfoPrivoder;
		this.metadataFormatProvider = metadataFormatProvider;
	}

	@Override
	public Collection<Record> getRecordCollection(Date from, Date until,
			String mdPrefix) {
		Collection<Record> result = new ArrayList<Record>();

		MetadataFormatImpl metadataFormat = (MetadataFormatImpl) getMetadataFormat(mdPrefix);

		for (SetInfo setInfo : setInfoPrivoder.getSetInfoCollection()) {
			SetInfoImpl setInfoImpl = (SetInfoImpl) setInfo;
			String[] pids = FedoraUtil.getAllInstances(setInfoImpl
					.getContentModel());
			for (String pid : pids) {
				Date lastModifiedDate = FedoraUtil.getLastModifiedDate(pid
						.split("/")[1]);
				if (lastModifiedDate.after(from)
						&& lastModifiedDate.before(until)) {
					FedoraURI fedoraURI = new FedoraURI(URI.create(pid + "/"
							+ metadataFormat.getDatastream()));
					result.add(buildRecord(fedoraURI, metadataFormat, setInfo,
							lastModifiedDate));
				}
			}
		}

		return result;
	}

	private Record buildRecord(FedoraURI fedoraURI,
			MetadataFormat metadataFormat, SetInfo setInfo,
			Date lastModifiedDate) {
		try {
			return new RecordImpl(fedoraURI.getPid(),
					metadataFormat.getPrefix(), lastModifiedDate,
					setInfo.getSetSpec(), StaticURI.toStaticFileUri(fedoraURI)
							.toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private MetadataFormat getMetadataFormat(String mdPrefix) {
		MetadataFormat metadataFormat = null;
		for (MetadataFormat md : metadataFormatProvider
				.getMetadataFormatCollection()) {
			if (md.getPrefix().equals(mdPrefix)) {
				metadataFormat = md;
			}
		}
		if (metadataFormat == null) {
			throw new NoMetadataFormatsException();
		}

		return metadataFormat;
	}
}
