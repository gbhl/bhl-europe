package com.bhle.access.jms.util;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

public class BatchIngestJsonMessageConverter implements MessageConverter {

	private static final Logger logger = LoggerFactory
			.getLogger(BatchIngestJsonMessageConverter.class);
	
	@Override
	public Message toMessage(Object object, Session session)
			throws JMSException, MessageConversionException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object fromMessage(Message message) throws JMSException,
			MessageConversionException {
		TextMessage tm = (TextMessage) message;
		logger.debug("Receive message {}", tm.getText());
		return JSONObject.fromObject(tm.getText());
	}

}
