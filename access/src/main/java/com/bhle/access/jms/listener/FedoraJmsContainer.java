package com.bhle.access.jms.listener;

import java.util.List;

import javax.jms.Message;

import com.yourmediashelf.fedora.client.messaging.MessagingListener;

public class FedoraJmsContainer implements MessagingListener {
	private List<FedoraJmsListener> listeners;

	public void setListeners(List<FedoraJmsListener> listeners) {
		this.listeners = listeners;
	}

	public void onMessage(String clientId, Message message) {
		for (FedoraJmsListener listener : listeners) {
			listener.onMessage(message);
		}
	}

}
