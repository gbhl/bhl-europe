package com.bhle.access.download.convert;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import org.akubraproject.Blob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bhle.access.convert.AbstractDataStreamConverter;
import com.bhle.access.convert.AfterBatchIngestConvertor;
import com.bhle.access.download.BasicDownloadRequest;
import com.bhle.access.download.ContentType;
import com.bhle.access.download.DownloadGateway;
import com.bhle.access.download.generator.PageURIExtractor;
import com.bhle.access.download.generator.PageURIExtractorImpl;
import com.bhle.access.storage.LowLevelStorage;
import com.bhle.access.util.FedoraURI;
import com.bhle.access.util.Resolution;

public class CompleteJpegConverter extends AbstractDataStreamConverter
		implements AfterBatchIngestConvertor {

	private static final Logger logger = LoggerFactory
			.getLogger(CompleteJpegConverter.class);

	@Autowired
	private DownloadGateway downloadGateway;

	@Autowired
	private LowLevelStorage lowLevelStorage;

	private static PageURIExtractor PID_EXTRACTOR = new PageURIExtractorImpl();

	@Override
	public String[] getContentModels() {
		return new String[] {};
	}

	@Override
	public String getDatastreamId() {
		return "FULL_JPEG";
	}

	@Override
	public String getDerivativeId() {
		return "FULL_JPEG";
	}

	@Override
	public String getDerivativeSuffix() {
		return "zip";
	}

	@Override
	public String getDerivativeMimeType() {
		return "application/x-zip-compressed";
	}

	@Override
	public void convert(String guid) {

		logger.info("Convert bhle:" + guid
				+ " to full JPEG at medium resoltuion");

		BasicDownloadRequest request = new BasicDownloadRequest();
		Blob blob = lowLevelStorage.getBlob(FedoraURI.getPidFromGuid(guid),
				getDerivativeId());
		request.setBlob(blob);
		request.setContentType(ContentType.JPEG);
		request.setResolution(new Resolution("medium"));
		String[] pageUris = PID_EXTRACTOR.getPageURIs(guid, "");
		request.setPageURIs(pageUris);
		downloadGateway.download(request);
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return null;
	}

}
