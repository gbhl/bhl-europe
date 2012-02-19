package com.bhle.access.jms.util;

public enum Method {
	INGEST, ADD_DATASTREAM, MODIFY_OBJECT, MODIFY_DATASTREAM, PURGE_OBJECT, PURGE_DATASTREAM;

	public static Method getInstance(String methodName) {
		if (methodName.equals("ingest")) {
			return INGEST;
		} else if (methodName.equals("addDatastream")) {
			return ADD_DATASTREAM;
		} else if (methodName.equals("modifyObject")) {
			return MODIFY_OBJECT;
		} else if (methodName.startsWith("modifyDatastream")) {
			return MODIFY_DATASTREAM;
		} else if (methodName.equals("purgeObject")) {
			return PURGE_OBJECT;
		} else if (methodName.equals("purgeDatastream")) {
			return PURGE_DATASTREAM;
		} else {
			throw new UnsupportedOperationException("Unhandelable method: "
					+ methodName);
		}
	}
}
