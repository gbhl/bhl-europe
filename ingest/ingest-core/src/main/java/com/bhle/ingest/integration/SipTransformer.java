package com.bhle.ingest.integration;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.bhle.ingest.Sip;

@Component
public class SipTransformer {
	private static final Logger logger = LoggerFactory
			.getLogger(SipTransformer.class);
	
	@Transformer
	public Message<Sip> transformer(Message<File> message){
		File payloadFile = message.getPayload();
		logger.info("Transform File to Sip: " + payloadFile);
		Sip sip = new Sip(payloadFile.toURI());
		return MessageBuilder.withPayload(sip).build();
	}
}
