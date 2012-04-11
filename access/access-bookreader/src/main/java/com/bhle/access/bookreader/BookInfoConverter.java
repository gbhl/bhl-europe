package com.bhle.access.bookreader;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.convert.AbstractDataStreamConverter;
import com.bhle.access.convert.AfterBatchIngestConvertor;

public class BookInfoConverter extends AbstractDataStreamConverter implements
		AfterBatchIngestConvertor {

	private static final Logger logger = LoggerFactory
			.getLogger(BookInfoConverter.class);
	
	public String[] getContentModels() {
		return new String[0];
	}

	public String getDatastreamId() {
		return "BOOKREADER";
	}

	public String getDerivativeId() {
		return "BOOKREADER";
	}

	public String getDerivativeSuffix() {
		return "json";
	}

	public String getDerivativeMimeType() {
		return "application/json";
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return null;
	}

	@Override
	public void convert(String guid) {
		logger.info("Convert bhle:" + guid + " to bookreader.json");
		
		BookInfo info = BookInfoBuilder.build(guid);
		BookInfoBuilder.save(info);
	}

}
