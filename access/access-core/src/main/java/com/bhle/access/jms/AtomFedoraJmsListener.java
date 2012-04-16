package com.bhle.access.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.bhle.access.jms.util.FedoraAtomMessage;

public class AtomFedoraJmsListener {

	private static final Logger logger = LoggerFactory
			.getLogger(AtomFedoraJmsListener.class);

	@Autowired
	@Value("generateDerivatives")
	private Job generateDerivativesJob;
	
	@Autowired
	private JobLauncher jobLauncher;
	
	public void onMessage(FedoraAtomMessage message) {

		switch (message.getMethodName()) {
		// case INGEST:
		// logger.debug("Generate derivatives after ingest");
		// DigitalObjectWrapper objectWrapper = DigitalObjectFactory
		// .build(message);
		// Derivative[] objectDerivatives = ConverterManager
		// .derive(objectWrapper);
		// for (Derivative derivative : objectDerivatives) {
		// storageService.updateDerivative(derivative);
		// derivative.close();
		// }
		// break;
		// case PURGE_OBJECT:
		// logger.debug("Delete derivatives after purgeObject");
		// storageService.deleteObject(message.getPid());
		// break;
		// case ADD_DATASTREAM:
		// logger.debug("Generate derivatives after addDatastream");
		// case MODIFY_DATASTREAM:
		// logger.debug("Delete derivatives after modifyDatastream");
		// DatastreamWrapper datastreamWrapper = DatastreamFactory
		// .build(message);
		// Derivative[] datastreamDerivatives = ConverterManager
		// .derive(datastreamWrapper);
		// for (Derivative derivative : datastreamDerivatives) {
		// storageService.updateDerivative(derivative);
		// derivative.close();
		// }
		// break;
		// case PURGE_DATASTREAM:
		// logger.debug("Delete derivatives after purgeDatastream");
		// DatastreamWrapper datastream = DatastreamFactory
		// .buildInformation(message);
		// Derivative[] derivativesInformation = ConverterManager
		// .deriveInformation(datastream);
		// for (Derivative derivative : derivativesInformation) {
		// storageService.deleteDerivative(derivative);
		// }
		// break;
		case MODIFY_OBJECT:
			// Nothing to generate
			logger.info("Generate derivatives after modifyObject");
			break;
		default:
			break;
		}

	}
}
