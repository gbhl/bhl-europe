package com.bhle.access.download.convert;

import java.io.InputStream;

import com.bhle.access.convert.AbstractDataStreamConverter;

public class FullJpegConverter extends AbstractDataStreamConverter {

	@Override
	public String[] getContentModels() {
		return new String[] {};
	}

	@Override
	public String getDatastreamId() {
		return "FULL_JPG";
	}

	@Override
	public String getDerivativeId() {
		return "FULL_JPG";
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
	public InputStream doConvert(InputStream inputStream) {
		return null;
	}

}
