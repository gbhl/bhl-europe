package com.bhle.access.convert;

import java.io.InputStream;

public class Ocr2OcrConverter extends AbstractDataStreamConverter {

	private static String[] contentModels;
	
	public static void setContentModels(String[] contentModels) {
		Ocr2OcrConverter.contentModels = contentModels;
	}

	@Override
	public InputStream doConvert(InputStream inputStream) {
		return inputStream;
	}

	public String[] getContentModels() {
		return contentModels;
	}

	public String getDatastreamId() {
		return "OCR";
	}

	public String getDerivativeId() {
		return "OCR";
	}

	public String getDerivativeSuffix() {
		return "txt";
	}

	public String getDerivativeMimeType() {
		return "text/plain";
	}

}
