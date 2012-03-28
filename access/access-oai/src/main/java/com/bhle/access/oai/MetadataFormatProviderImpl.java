package com.bhle.access.oai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import proai.MetadataFormat;

public class MetadataFormatProviderImpl implements MetadataFormatProvider {

	private static String METADATAFORMAT_PREFIX = "metadataFormat.";
	private static String METADATAFORMAT_PREFIX_SUFFIX = "prefix";
	private static String METADATAFORMAT_DATASTREAM_SUFFIX = "datastream";
	private static String METADATAFORMAT_NAMESPACE_SUFFIX = "namespace";
	private static String METADATAFORMAT_SCHEMALOCATION_SUFFIX = "schemaLocation";

	private Properties props;
	
	public MetadataFormatProviderImpl(Properties props) {
		super();
		this.props = props;
	}

	@Override
	public Collection<MetadataFormat> getMetadataFormatCollection() {
		Map<String, Map<String, String>> metadataFormatMap = new HashMap<String, Map<String, String>>();

		for (Object keyObject : props.keySet()) {
			String key = (String) keyObject;
			if (key.startsWith(METADATAFORMAT_PREFIX)) {
				String[] parts = key.split("\\.");
				String metadataFormatKey = parts[1];
				String suffix = parts[2];
				Map<String, String> subMap = metadataFormatMap
						.get(metadataFormatKey);
				if (subMap == null) {
					subMap = new HashMap<String, String>();
					metadataFormatMap.put(metadataFormatKey, subMap);
				}
				subMap.put(suffix, props.getProperty(key));
			}
		}

		Collection<MetadataFormat> result = new ArrayList<MetadataFormat>();
		for (String key : metadataFormatMap.keySet()) {
			Map<String, String> metadataFormatSubMap = metadataFormatMap
					.get(key);
			MetadataFormat metadataFormat = new MetadataFormatImpl(
					metadataFormatSubMap.get(METADATAFORMAT_PREFIX_SUFFIX),
					metadataFormatSubMap.get(METADATAFORMAT_DATASTREAM_SUFFIX),
					metadataFormatSubMap.get(METADATAFORMAT_NAMESPACE_SUFFIX),
					metadataFormatSubMap
							.get(METADATAFORMAT_SCHEMALOCATION_SUFFIX));
			result.add(metadataFormat);
		}

		return result;
	}
}
