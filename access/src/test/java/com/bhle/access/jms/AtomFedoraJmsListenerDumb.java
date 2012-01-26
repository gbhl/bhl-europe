package com.bhle.access.jms;

import javax.jms.JMSException;
import javax.jms.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

import com.bhle.access.convert.ConvertorManager;
import com.bhle.access.domain.DatastreamFactory;
import com.bhle.access.domain.DatastreamWrapper;
import com.bhle.access.domain.Derivative;
import com.bhle.access.domain.DigitalObjectFactory;
import com.bhle.access.domain.DigitalObjectWrapper;
import com.bhle.access.jms.listener.FedoraJmsListener;
import com.bhle.access.jms.util.FedoraAtomMessage;
import com.bhle.access.storage.StorageService;

@Component
public class AtomFedoraJmsListenerDumb implements FedoraJmsListener {

	private static final Logger logger = LoggerFactory
			.getLogger(AtomFedoraJmsListenerDumb.class);

	@Autowired
	private MessageConverter messageConverter;

	private static FedoraAtomMessage ATOM_MESSAGE;

	public void onMessage(Message message) {
		ATOM_MESSAGE = convertMessage(message);

		switch (ATOM_MESSAGE.getMethodName()) {
		case INGEST:
			logger.info("Generate derivatives after ingest");
			break;
		case PURGE_OBJECT:
			// TODO: delete the whole folder
			logger.info("Generate derivatives after purgeObject");
			break;
		case ADD_DATASTREAM:
			logger.info("Generate derivatives after addDatastream");
		case MODIFY_DATASTREAM:
			logger.info("Delete derivatives after modifyDatastream");
			break;
		case PURGE_DATASTREAM:
			logger.info("Delete derivatives after purgeDatastream");
			break;
		case MODIFY_OBJECT:
			// Nothing to generate
			logger.info("Generate derivatives after modifyObject");
			break;
		default:
			throw new IllegalArgumentException();
		}

	}

	private FedoraAtomMessage convertMessage(Message message) {
		try {
			return (FedoraAtomMessage) messageConverter.fromMessage(message);
		} catch (MessageConversionException e) {
			e.printStackTrace();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return ATOM_MESSAGE;
	}

	public FedoraAtomMessage getAtomMessage() {
		return ATOM_MESSAGE;
	}

}
