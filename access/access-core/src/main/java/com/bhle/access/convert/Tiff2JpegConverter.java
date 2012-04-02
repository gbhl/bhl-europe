package com.bhle.access.convert;

import java.io.InputStream;

import com.bhle.access.util.ImageUtil;

public class Tiff2JpegConverter extends AbstractDataStreamConverter {

	private static String[] contentModels;
	
	public static void setContentModels(String[] contentModels) {
		Tiff2JpegConverter.contentModels = contentModels;
	}


	public String[] getContentModels() {
		return contentModels;
	}

	public String getDatastreamId() {
		return "TIFF";
	}

	public String getDerivativeId() {
		return "JP2";
	}

	public String getDerivativeSuffix() {
		return "jp2";
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return ImageUtil.tiffToJp2(inputStream);
	}

	public String getDerivativeMimeType() {
		return "image/jp2";
	}

}
