package com.bhle.access.jms.util;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

public class FedoraJmsMessageConverter implements MessageConverter{

	public Object fromMessage(Message message) throws JMSException,
			MessageConversionException {
		TextMessage tm = (TextMessage) message;
		FedoraAtomMessage atomMessage = new FedoraAtomMessage(tm.getText());
		return atomMessage;
	}

	public Message toMessage(Object object, Session session) throws JMSException,
			MessageConversionException {
		//TODO: not implemented
		return null;
	}

}
