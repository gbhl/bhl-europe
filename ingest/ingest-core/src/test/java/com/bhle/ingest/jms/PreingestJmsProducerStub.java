package com.bhle.ingest.jms;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

@Component
public class PreingestJmsProducerStub {

	@Autowired
	@Qualifier(value = "preingestJmsProducerTemplate")
	private JmsTemplate template;
	
	public void send(final Map<String, String> messageBody) {
		template.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				MapMessage msg = session.createMapMessage();
				for(String key : messageBody.keySet()){
					msg.setString(key, messageBody.get(key));
				}
				return msg;
			}
		});
	}

}
