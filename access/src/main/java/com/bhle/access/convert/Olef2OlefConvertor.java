package com.bhle.access.convert;

import java.io.InputStream;

public class Olef2OlefConvertor extends AbstractDataStreamConvertor {

	private static String[] contentModels;
	
	public static void setContentModels(String[] contentModels) {
		Olef2OlefConvertor.contentModels = contentModels;
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return inputStream;
	}

	public String[] getContentModels() {
		return contentModels;
	}

	public String getDatastreamId() {
		return "OLEF";
	}

	public String getDerivativeId() {
		return "OLEF";
	}

	public String getDerivativeSuffix() {
		return "xml";
	}

	public String getDerivativeMimeType() {
		return "text/xml";
	}

}
