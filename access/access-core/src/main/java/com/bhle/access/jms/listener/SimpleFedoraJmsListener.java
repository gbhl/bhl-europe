package com.bhle.access.jms.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SimpleFedoraJmsListener implements FedoraJmsListener {

	private static final Logger logger = LoggerFactory
			.getLogger(SimpleFedoraJmsListener.class);

	private String plainMessage;

	public void onMessage(Message message) {
		TextMessage tm = (TextMessage) message;
		try {
			this.plainMessage = tm.getText();

			logger.info("Receive a message: " + plainMessage);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public String getPlainMessage() {
		return plainMessage;
	}

}
