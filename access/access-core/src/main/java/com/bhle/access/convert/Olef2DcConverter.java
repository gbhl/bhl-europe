package com.bhle.access.convert;

import java.io.InputStream;

import com.bhle.access.util.MetadataUtil;

public class Olef2DcConverter extends AbstractDataStreamConverter{

	private static String[] contentModels;
	
	public static void setContentModels(String[] contentModels) {
		Olef2DcConverter.contentModels = contentModels;
	}
	
	public String[] getContentModels() {
		return contentModels;
	}

	public String getDatastreamId() {
		return "OLEF";
	}

	public String getDerivativeId() {
		return "DC";
	}

	public String getDerivativeSuffix() {
		return "xml";
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return MetadataUtil.olefToDc(inputStream);
	}

	public String getDerivativeMimeType() {
		return "text/xml";
	}

}
