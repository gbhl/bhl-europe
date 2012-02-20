package com.bhle.access.jms.util;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import com.yourmediashelf.fedora.client.messaging.MessagingClient;
import com.yourmediashelf.fedora.client.messaging.MessagingException;
import com.yourmediashelf.fedora.client.messaging.MessagingListener;

public class SimpleFedoraJmsClient {

	public static class FedoraJmsClient implements MessagingListener {
		MessagingClient messagingClient;

		public void start() throws MessagingException {
			Properties properties = new Properties();
			properties.setProperty("java.naming.factory.initial",
					"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
			properties.setProperty("java.naming.provider.url",
					"tcp://localhost:61616");
			properties.setProperty("connection.factory.name",
					"ConnectionFactory");
			properties.setProperty("topic.fedora", "fedora.apim.update");
			messagingClient = new MessagingClient("test", this, properties,
					true);
			messagingClient.start();
		}

		public void stop() throws MessagingException {
			messagingClient.stop(false);
		}

		public void onMessage(String clientId, Message message) {
			String messageText = "";
			try {
				messageText = ((TextMessage) message).getText();
			} catch (JMSException e) {
				System.err.println("Error retrieving message text "
						+ e.getMessage());
			}
			System.out.println(messageText);
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FedoraJmsClient client = new FedoraJmsClient();
		try {
			client.start();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}
