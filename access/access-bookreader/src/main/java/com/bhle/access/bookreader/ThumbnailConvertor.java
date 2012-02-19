package com.bhle.access.bookreader;

import java.io.InputStream;

import com.bhle.access.convert.AbstractDataStreamConvertor;

public class ThumbnailConvertor extends AbstractDataStreamConvertor{

	public String[] getContentModels() {
		return new String[0];
	}

	public String getDatastreamId() {
		return "THUMBNAIL";
	}

	public String getDerivativeId() {
		return "THUMBNAIL";
	}

	public String getDerivativeSuffix() {
		return "jpg";
	}

	public String getDerivativeMimeType() {
		return "image/jpeg";
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return null;
	}

}
