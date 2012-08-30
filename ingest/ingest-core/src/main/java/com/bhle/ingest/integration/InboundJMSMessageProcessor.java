package com.bhle.ingest.integration;

import java.net.URI;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;

import com.bhle.ingest.Sip;

public class InboundJMSMessageProcessor {

	private static final Logger logger = LoggerFactory
			.getLogger(InboundJMSMessageProcessor.class);

	private static String MSG_GUID_NAME;
	private static String MSG_URI_NAME;

	@Value("${jms.message.guid}")
	public void setGuidName(String guidName) {
		MSG_GUID_NAME = guidName;
	}

	@Value("${jms.message.uri}")
	public void setUriName(String uriName) {
		MSG_URI_NAME = uriName;
	}

	@ServiceActivator
	public Message<Sip> handleIncomingJmsMessage(
			Message<String> inboundJmsMessage) throws Throwable {
		String msg = inboundJmsMessage.getPayload();
		JSONObject json = JSONObject.fromObject(msg);
		String guid = json.getString(MSG_GUID_NAME);
		String uri = json.getString(MSG_URI_NAME);

		if (!guid.startsWith("bhle:")) {
			guid = "bhle:" + guid;
		}

		if (uri == null || uri.equals("")) {
			throw new IllegalArgumentException("Message must contain URI");
		}

		logger.info("Receive JMS message: {}", msg);

		Sip sip = new Sip(guid, URI.create(uri));
		Message<Sip> message = MessageBuilder.withPayload(sip).build();

		logger.info("Returning Message<Sip>: {}", message.toString());

		return message;
	}
}
