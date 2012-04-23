package com.bhle.ingest.batch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;

import com.bhle.ingest.FedoraServiceImpl;
import com.bhle.ingest.Sip;
import com.bhle.ingest.jms.IngestJmsProducer;

public class BatchIngestListener {

	private static final Logger logger = LoggerFactory
			.getLogger(BatchIngestListener.class);

	private IngestJmsProducer jmsProducer;

	public void setJmsProducer(IngestJmsProducer jmsProducer) {
		this.jmsProducer = jmsProducer;
	}

	private FedoraServiceImpl fedoraService;

	public void setFedoraService(FedoraServiceImpl fedoraService) {
		this.fedoraService = fedoraService;
	}

	@AfterJob
	public void afterJob(JobExecution jobExecution) {
		reportViaJms(jobExecution);

		String guid = jobExecution.getJobInstance().getJobParameters()
				.getString(Sip.JOB_PARAM_GUID_KEY);

		if (jobExecution.getStatus().isUnsuccessful()) {
			purgeItem(guid);
		} else {
			//activateItem(guid);
		}
	}

	private void purgeItem(String guid) {
		String pid = guid.replace("/", "-");
		logger.info("Delete all members of object: {}", pid);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		fedoraService.purgeAllMembers(pid);

	}

//	private void activateItem(String guid) {
//		String pid = guid.replace("/", "-");
//		logger.info("Activate object: {}", pid);
//		fedoraService.activate(pid);
//	}

	private void reportViaJms(JobExecution jobExecution) {
		Map<String, String> messageBody = new HashMap<String, String>();
		messageBody.put("GUID", jobExecution.getJobInstance()
				.getJobParameters().getString(Sip.JOB_PARAM_GUID_KEY));
		messageBody.put("STATUS", jobExecution.getStatus().toString());
		if (jobExecution.getStatus().isUnsuccessful()) {
			StringBuffer exceptions = new StringBuffer();
			List<Throwable> throwables = jobExecution.getAllFailureExceptions();
			for (Throwable throwable : throwables) {
				exceptions.append(throwable.toString());
			}
			messageBody.put("EXCEPTIONS", exceptions.toString());
		}
		logger.info("Send Ingest status message: {}", messageBody);
		jmsProducer.send(messageBody);
	}
}
