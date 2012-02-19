package com.bhle.access.convert;

import java.io.InputStream;

import com.bhle.access.util.ImageUtil;

public class Tiff2SizeConvertor extends AbstractDataStreamConvertor {

	private static String[] contentModels;

	public static void setContentModels(String[] contentModels) {
		Tiff2SizeConvertor.contentModels = contentModels;
	}

	public String[] getContentModels() {
		return contentModels;
	}

	public String getDatastreamId() {
		return "TIFF";
	}

	public String getDerivativeId() {
		return "IMAGE_INFO";
	}

	public String getDerivativeSuffix() {
		return "txt";
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return ImageUtil.tiffToJp2Size(inputStream);
	}

	public String getDerivativeMimeType() {
		return "text/plain";
	}

}
