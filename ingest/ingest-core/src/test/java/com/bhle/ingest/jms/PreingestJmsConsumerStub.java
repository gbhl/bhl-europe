package com.bhle.ingest.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreingestJmsConsumerStub {
	
	private static final Logger logger = LoggerFactory
			.getLogger(PreingestJmsConsumerStub.class);
	
	public void onMessage(String msg){
		logger.info("Preingest Receive Message: {}", msg);
	}
}	
