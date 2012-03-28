package com.bhle.access.oai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import proai.SetInfo;

public class SetInfoProviderImpl implements SetInfoPrivoder {

	private static String SETINFO_PREFIX = "setInfo.";
	private static String SETINFO_NAME_SUFFIX = "name";
	private static String SETINFO_SPEC_SUFFIX = "spec";
	private static String SETINFO_CONTENT_MODEL_SUFFIX = "contentModel";

	private Properties props;

	public SetInfoProviderImpl(Properties props) {
		super();
		this.props = props;
	}

	@Override
	public Collection<SetInfo> getSetInfoCollection() {
		Map<String, Map<String, String>> setInfoMap = new HashMap<String, Map<String, String>>();

		for (Object keyObject : props.keySet()) {
			String key = (String) keyObject;
			if (key.startsWith(SETINFO_PREFIX)) {
				String[] parts = key.split("\\.");
				String setInfoKey = parts[1];
				String suffix = parts[2];
				Map<String, String> subMap = setInfoMap.get(setInfoKey);
				if (subMap == null) {
					subMap = new HashMap<String, String>();
					setInfoMap.put(setInfoKey, subMap);
				}
				subMap.put(suffix, props.getProperty(key));
			}
		}

		Collection<SetInfo> result = new ArrayList<SetInfo>();
		for (String key : setInfoMap.keySet()) {
			Map<String, String> setInfoSubMap = setInfoMap.get(key);
			SetInfo setInfo = new SetInfoImpl(
					setInfoSubMap.get(SETINFO_SPEC_SUFFIX),
					setInfoSubMap.get(SETINFO_NAME_SUFFIX),
					setInfoSubMap.get(SETINFO_CONTENT_MODEL_SUFFIX));
			result.add(setInfo);
		}

		return result;
	}
}
