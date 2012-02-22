package com.bhle.access.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.bhle.access.jms.util.FedoraAtomMessage;

@Component
public class AtomFedoraJmsListenerDumb {

	private static final Logger logger = LoggerFactory
			.getLogger(AtomFedoraJmsListenerDumb.class);

	private static FedoraAtomMessage ATOM_MESSAGE;

	public void onMessage(FedoraAtomMessage message) {
		ATOM_MESSAGE = message;

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

	public FedoraAtomMessage getAtomMessage() {
		return ATOM_MESSAGE;
	}

}
