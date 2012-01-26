package com.bhle.access.jms;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yourmediashelf.fedora.client.messaging.MessagingClient;
import com.yourmediashelf.fedora.client.messaging.MessagingException;

@Component
public class FedoraJmsInitializer {
	@Autowired (required = true)
	private MessagingClient messagingClient;

	
	@PostConstruct
	public void start() throws MessagingException{
        messagingClient.start(false);
	}

	@PreDestroy
	public void stop() throws MessagingException {
		messagingClient.stop(true);
	}
}
