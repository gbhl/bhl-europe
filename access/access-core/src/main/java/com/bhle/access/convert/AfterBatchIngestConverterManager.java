package com.bhle.access.convert;

import java.util.List;

public class AfterBatchIngestConverterManager {
	private static List<AfterBatchIngestConvertor> converters;

	public void setConverters(List<AfterBatchIngestConvertor> convertors) {
		AfterBatchIngestConverterManager.converters = convertors;
	}

	public static void allConvert(String guid) {
		for (AfterBatchIngestConvertor convertor : converters) {
			convertor.convert(guid);
		}
	}
}
