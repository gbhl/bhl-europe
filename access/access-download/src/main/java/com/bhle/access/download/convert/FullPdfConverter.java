package com.bhle.access.download.convert;

import java.io.InputStream;

import com.bhle.access.convert.AbstractDataStreamConverter;

public class FullPdfConverter extends AbstractDataStreamConverter {

	@Override
	public String[] getContentModels() {
		return new String[] {};
	}

	@Override
	public String getDatastreamId() {
		return "FULL_PDF";
	}

	@Override
	public String getDerivativeId() {
		return "FULL_PDF";
	}

	@Override
	public String getDerivativeSuffix() {
		return "pdf";
	}

	@Override
	public String getDerivativeMimeType() {
		return "application/pdf";
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return null;
	}

}
