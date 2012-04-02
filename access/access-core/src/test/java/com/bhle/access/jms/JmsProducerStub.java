package com.bhle.access.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class JmsProducerStub implements JmsProducer {

	@Autowired
	@Qualifier(value = "jmsProducerTemplate")
	private JmsTemplate template;

	public void send(final String text) {
		template.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				TextMessage msg = session.createTextMessage(text);
				return msg;
			}
		});
	}

}
