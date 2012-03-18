package com.bhle.ingest.integration;

import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;

import com.bhle.ingest.Sip;

public class InboundJMSMessageProcessor {

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

	private static final Logger logger = LoggerFactory
			.getLogger(InboundJMSMessageProcessor.class);

	@ServiceActivator
	public Message<Sip> handleIncomingJmsMessage(
			Message<Map<String, Object>> inboundJmsMessage) throws Throwable {
		Map<String, Object> msg = inboundJmsMessage.getPayload();
		String guid = (String) msg.get(MSG_GUID_NAME);
		String uri = (String) msg.get(MSG_URI_NAME);

		if (uri == null || uri.equals("")) {
			throw new IllegalArgumentException("Message must contain URI");
		}

		logger.info("Receive JMS message: GUID:{}; URI:{}", guid, uri);

		Sip sip = new Sip(guid, URI.create(uri));

		return MessageBuilder.withPayload(sip).build();
	}
}
