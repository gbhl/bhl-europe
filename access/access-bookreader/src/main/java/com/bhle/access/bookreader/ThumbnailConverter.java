package com.bhle.access.bookreader;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.convert.AbstractDataStreamConverter;
import com.bhle.access.convert.AfterBatchIngestConvertor;

public class ThumbnailConverter extends AbstractDataStreamConverter implements
		AfterBatchIngestConvertor {
	
	private static final Logger logger = LoggerFactory
			.getLogger(ThumbnailConverter.class);

	public String[] getContentModels() {
		return new String[0];
	}

	public String getDatastreamId() {
		return "THUMBNAIL";
	}

	public String getDerivativeId() {
		return "THUMBNAIL";
	}

	public String getDerivativeSuffix() {
		return "jpg";
	}

	public String getDerivativeMimeType() {
		return "image/jpeg";
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return null;
	}

	@Override
	public void convert(String guid) {
		logger.info("Convert bhle:" + guid + " to thumbnail");
		
		InputStream in = ThumbnailBuilder.build(guid);
		ThumbnailBuilder.save(guid, in);
	}

}
