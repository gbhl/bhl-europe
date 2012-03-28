package com.bhle.ingest.jms;

import java.util.Map;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class IngestJmsProducer {

	@Autowired
	@Qualifier("ingestJmsProducerTemplate")
	private JmsTemplate template;
	
	public void send(final Map<String, String> messageBody) {
		template.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				JSONObject json = new JSONObject();
				for (String key : messageBody.keySet()) {
					json.put(key, messageBody.get(key));
				}
				TextMessage msg = session.createTextMessage(json.toString());
				return msg;
			}
		});
	}

}
