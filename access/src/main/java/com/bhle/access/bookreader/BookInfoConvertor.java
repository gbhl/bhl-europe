package com.bhle.access.bookreader;

import java.io.InputStream;

import com.bhle.access.convert.AbstractDataStreamConvertor;

public class BookInfoConvertor extends AbstractDataStreamConvertor{

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

}
