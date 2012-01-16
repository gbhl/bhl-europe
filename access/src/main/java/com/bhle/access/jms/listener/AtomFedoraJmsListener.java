package com.bhle.access.jms.listener;

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
import com.bhle.access.jms.util.FedoraAtomMessage;
import com.bhle.access.storage.StorageService;

@Component
public class AtomFedoraJmsListener implements FedoraJmsListener {

	private static final Logger logger = LoggerFactory
			.getLogger(AtomFedoraJmsListener.class);

	@Autowired(required = true)
	private MessageConverter messageConverter;

	private FedoraAtomMessage atomMessage;

	@Autowired(required = true)
	private StorageService storageService;

	public void onMessage(Message message) {
		atomMessage = convertMessage(message);

		switch (atomMessage.getMethodName()) {
		case INGEST:
			logger.info("Generate derivatives after ingest");
			DigitalObjectWrapper objectWrapper = DigitalObjectFactory
					.build(atomMessage);
			Derivative[] objectDerivatives = ConvertorManager
					.derive(objectWrapper);
			for (Derivative derivative : objectDerivatives) {
				storageService.updateDerivative(derivative);
			}
			break;
		case PURGE_OBJECT:
			// TODO: delete the whole folder
			logger.info("Generate derivatives after purgeObject");
			storageService.deleteObject(atomMessage.getPid());
			break;
		case ADD_DATASTREAM:
			logger.info("Generate derivatives after addDatastream");
		case MODIFY_DATASTREAM:
			logger.info("Delete derivatives after modifyDatastream");
			DatastreamWrapper datastreamWrapper = DatastreamFactory
					.build(atomMessage);
			Derivative[] datastreamDerivatives = ConvertorManager
					.derive(datastreamWrapper);
			for (Derivative derivative : datastreamDerivatives) {
				storageService.updateDerivative(derivative);
			}
			break;
		case PURGE_DATASTREAM:
			logger.info("Delete derivatives after purgeDatastream");
			DatastreamWrapper datastream = DatastreamFactory
					.buildInformation(atomMessage);
			Derivative[] derivativesInformation = ConvertorManager
					.deriveInformation(datastream);
			for (Derivative derivative : derivativesInformation) {
				storageService.deleteDerivative(derivative);
			}
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
		return atomMessage;
	}

	public FedoraAtomMessage getAtomMessage() {
		return atomMessage;
	}

}
