package com.bhle.access.convert;

import java.util.List;

public class AfterBatchIngestConverterManager {
	private static List<AfterBatchIngestConvertor> converters;

	public void setConverters(List<AfterBatchIngestConvertor> convertors) {
		if (AfterBatchIngestConverterManager.converters == null) {
			AfterBatchIngestConverterManager.converters = convertors;
		} else {
			AfterBatchIngestConverterManager.converters.addAll(convertors);
		}
	}

	public static List<AfterBatchIngestConvertor> getConverters() {
		return converters;
	}

	public static void allConvert(String guid) {
		for (AfterBatchIngestConvertor convertor : converters) {
			convertor.convert(guid);
		}
	}
}
