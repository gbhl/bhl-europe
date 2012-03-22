package com.bhle.access.convert;

import java.io.InputStream;

import org.springframework.core.io.Resource;

import com.bhle.access.util.MetadataUtil;

public class Olef2EseConvertor extends AbstractDataStreamConvertor {

	private Resource xslt;

	private static String[] contentModels;

	public static void setContentModels(String[] contentModels) {
		Olef2EseConvertor.contentModels = contentModels;
	}

	@Override
	public String[] getContentModels() {
		return contentModels;
	}

	@Override
	public String getDatastreamId() {
		return "OLEF";
	}

	@Override
	public String getDerivativeId() {
		return "ESE";
	}

	@Override
	public String getDerivativeSuffix() {
		return "xml";
	}

	@Override
	public String getDerivativeMimeType() {
		return "text/xml";
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return MetadataUtil.olefToEse(inputStream);
	}

}
