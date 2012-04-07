package com.bhle.ingest.batch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.bhle.ingest.FedoraServiceImpl;
import com.bhle.ingest.Sip;
import com.bhle.ingest.jms.IngestJmsProducer;

@Component
public class BatchIngestListener {

	private static final Logger logger = LoggerFactory
			.getLogger(BatchIngestListener.class);

	@Autowired
	@Qualifier("ingestJmsProducer")
	private IngestJmsProducer jmsProducer;

	@Autowired
	private FedoraServiceImpl service;

	@AfterJob
	public void afterJob(JobExecution jobExecution) {
		reportViaJms(jobExecution);
		activateItem(jobExecution);
	}

	private void activateItem(JobExecution jobExecution) {
		try {
			// give enough time for Fedora to build index
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			String guid = jobExecution.getJobInstance().getJobParameters()
					.getString(Sip.JOB_PARAM_GUID_KEY);
			String pid = guid.replace("/", "-");
			logger.debug("Activate object: {}", pid);
			service.activate(pid);
		}
	}

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
