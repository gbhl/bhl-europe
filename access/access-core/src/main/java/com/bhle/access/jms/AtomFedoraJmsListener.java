package com.bhle.access.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.bhle.access.convert.ConverterManager;
import com.bhle.access.domain.DatastreamFactory;
import com.bhle.access.domain.DatastreamWrapper;
import com.bhle.access.domain.Derivative;
import com.bhle.access.domain.DigitalObjectFactory;
import com.bhle.access.domain.DigitalObjectWrapper;
import com.bhle.access.jms.util.FedoraAtomMessage;
import com.bhle.access.storage.StorageService;

public class AtomFedoraJmsListener {

	private static final Logger logger = LoggerFactory
			.getLogger(AtomFedoraJmsListener.class);

	private static StorageService storageService;
	
	@Autowired(required = true)
	public void setStorageService(StorageService storageService) {
		AtomFedoraJmsListener.storageService = storageService;
	}

	public void onMessage(FedoraAtomMessage message) {

		switch (message.getMethodName()) {
		case INGEST:
			logger.debug("Generate derivatives after ingest");
			DigitalObjectWrapper objectWrapper = DigitalObjectFactory
					.build(message);
			Derivative[] objectDerivatives = ConverterManager
					.derive(objectWrapper);
			for (Derivative derivative : objectDerivatives) {
				storageService.updateDerivative(derivative);
				derivative.close();
			}
			break;
		case PURGE_OBJECT:
			logger.debug("Delete derivatives after purgeObject");
			storageService.deleteObject(message.getPid());
			break;
		case ADD_DATASTREAM:
			logger.debug("Generate derivatives after addDatastream");
		case MODIFY_DATASTREAM:
			logger.debug("Delete derivatives after modifyDatastream");
			DatastreamWrapper datastreamWrapper = DatastreamFactory
					.build(message);
			Derivative[] datastreamDerivatives = ConverterManager
					.derive(datastreamWrapper);
			for (Derivative derivative : datastreamDerivatives) {
				storageService.updateDerivative(derivative);
				derivative.close();
			}
			break;
		case PURGE_DATASTREAM:
			logger.debug("Delete derivatives after purgeDatastream");
			DatastreamWrapper datastream = DatastreamFactory
					.buildInformation(message);
			Derivative[] derivativesInformation = ConverterManager
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
}
