package com.bhle.access.convert;

import java.io.InputStream;

import com.bhle.access.util.MetadataUtil;

public class Olef2BibTexConverter extends AbstractDataStreamConverter {

	private static String[] contentModels;

	public static void setContentModels(String[] contentModels) {
		Olef2BibTexConverter.contentModels = contentModels;
	}

	public String[] getContentModels() {
		return contentModels;
	}

	public String getDatastreamId() {
		return "OLEF";
	}

	public String getDerivativeId() {
		return "BIBTEX";
	}

	public String getDerivativeSuffix() {
		return "bib";
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return MetadataUtil.olefToBibText(inputStream);
	}

	public String getDerivativeMimeType() {
		return "text/plain";
	}

}
