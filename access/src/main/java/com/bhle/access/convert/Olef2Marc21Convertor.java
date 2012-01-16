package com.bhle.access.convert;

import java.io.InputStream;

import com.bhle.access.util.MetadataUtil;

public class Olef2Marc21Convertor extends AbstractDataStreamConvertor{

	private static String[] contentModels;
	
	public static void setContentModels(String[] contentModels) {
		Olef2Marc21Convertor.contentModels = contentModels;
	}
	
	public String[] getContentModels() {
		return contentModels;
	}

	public String getDatastreamId() {
		return "OLEF";
	}

	public String getDerivativeId() {
		return "MARC21";
	}

	public String getDerivativeSuffix() {
		return "xml";
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return MetadataUtil.olefToMarc21(inputStream);
	}

	public String getDerivativeMimeType() {
		return "text/xml";
	}

}
