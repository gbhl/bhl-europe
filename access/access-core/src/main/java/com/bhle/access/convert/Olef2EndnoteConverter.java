package com.bhle.access.convert;

import java.io.InputStream;

import com.bhle.access.util.MetadataUtil;

public class Olef2EndnoteConverter extends AbstractDataStreamConverter{

	private static String[] contentModels;
	
	public static void setContentModels(String[] contentModels) {
		Olef2EndnoteConverter.contentModels = contentModels;
	}
	
	public String[] getContentModels() {
		return contentModels;
	}

	public String getDatastreamId() {
		return "OLEF";
	}

	public String getDerivativeId() {
		return "ENDNOTE";
	}

	public String getDerivativeSuffix() {
		return "end";
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return MetadataUtil.olefToEndnote(inputStream);
	}

	public String getDerivativeMimeType() {
		return "text/plain";
	}

}
