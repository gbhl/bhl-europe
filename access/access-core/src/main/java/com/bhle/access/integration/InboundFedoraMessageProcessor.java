package com.bhle.access.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.bhle.access.jms.util.FedoraAtomMessage;
import com.bhle.access.jms.util.Method;

@Component
public class InboundFedoraMessageProcessor {

	private static final Logger logger = LoggerFactory
			.getLogger(InboundFedoraMessageProcessor.class);

	@ServiceActivator
	public Message<String> handleIncomingMessage(FedoraAtomMessage fedoraMessage)
			throws Throwable {
		if (fedoraMessage.getMethodName() == Method.MODIFY_OBJECT) {
			String state = fedoraMessage.getState();
			if (state != null && state.equals("A")) {
				if (!logger.isDebugEnabled()) {
					// wait enough time for Fedora to insert resource index
					Thread.sleep(10000);
				}
				return MessageBuilder.withPayload(fedoraMessage.getPid())
						.build();
			}
		}
		return null;
	}
}
