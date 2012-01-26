package com.bhle.access.convert;

import java.io.InputStream;

import com.bhle.access.util.MetadataUtil;

public class Olef2DcConvertor extends AbstractDataStreamConvertor{

	private static String[] contentModels;
	
	public static void setContentModels(String[] contentModels) {
		Olef2DcConvertor.contentModels = contentModels;
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
