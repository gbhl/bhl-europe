package com.bhle.access.jms.listener;

import javax.jms.Message;

public interface FedoraJmsListener {
	public void onMessage(Message message);
}
