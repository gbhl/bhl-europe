package com.bhle.access.jms;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bhle.access.convert.AfterBatchIngestConverterManager;

public class AfterBatchIngestJmsListener {

	private static final Logger logger = LoggerFactory
			.getLogger(AfterBatchIngestJmsListener.class);
	
	public void onMessage(JSONObject json) {
		String guid = json.getString("GUID");
		String status = json.getString("STATUS");
		if (!status.equals("COMPLETED")) {
			return;
		}
		
		logger.info("Start post-ingest generation...");

		AfterBatchIngestConverterManager.allConvert(guid);
	}
}
