package com.bhle.access.download.convert;

import java.io.InputStream;

import com.bhle.access.convert.AbstractDataStreamConverter;

public class FullOcrConverter extends AbstractDataStreamConverter {

	@Override
	public String[] getContentModels() {
		return new String[] {};
	}

	@Override
	public String getDatastreamId() {
		return "FULL_OCR";
	}

	@Override
	public String getDerivativeId() {
		return "FULL_OCR";
	}

	@Override
	public String getDerivativeSuffix() {
		return "txt";
	}

	@Override
	public String getDerivativeMimeType() {
		return "text/plain";
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return null;
	}

}
