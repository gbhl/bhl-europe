package com.bhle.access.convert;

import java.io.InputStream;

public class Ocr2OcrConvertor extends AbstractDataStreamConvertor {

	private static String[] contentModels;
	
	public static void setContentModels(String[] contentModels) {
		Ocr2OcrConvertor.contentModels = contentModels;
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
