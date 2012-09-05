package com.bhle.ingest.integration;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.integration.launch.JobLaunchRequest;

public class JMSToJobTransformer {

	private static final Logger logger = LoggerFactory
			.getLogger(JMSToJobTransformer.class);

	private static String MSG_GUID_NAME;
	private static String MSG_URI_NAME;

	@Autowired
	private JobLocator jobLocator;

	@Value("${jms.message.guid}")
	public void setGuidName(String guidName) {
		MSG_GUID_NAME = guidName;
	}

	@Value("${jms.message.uri}")
	public void setUriName(String uriName) {
		MSG_URI_NAME = uriName;
	}

	@Transformer
	public Message<JobLaunchRequest> handleIncomingJmsMessage(
			Message<String> inboundJmsMessage) throws Throwable {
		String msg = inboundJmsMessage.getPayload();
		JSONObject json = JSONObject.fromObject(msg);
		String guid = json.getString(MSG_GUID_NAME);
		String uri = json.getString(MSG_URI_NAME);

		logger.info("Receive JMS message: {}", msg);

		if (!guid.startsWith("bhle:")) {
			guid = "bhle:" + guid;
		}

		if (uri == null || uri.equals("")) {
			throw new IllegalArgumentException("Message must contain URI");
		}

		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addString("GUID", guid);
		jobParametersBuilder.addString("URI", uri);
		JobParameters jobParameters = jobParametersBuilder.toJobParameters();
		
		Job job = null;
		try {
			job = jobLocator.getJob("batchIngestJob");
		} catch (NoSuchJobException e) {
			e.printStackTrace();
		}
		JobLaunchRequest jobLaunchRequest = new JobLaunchRequest(job, jobParameters);
		
		logger.info("jobLaunchRequest done - returning Message");
		
		Message<JobLaunchRequest> jbrMessage = MessageBuilder.withPayload(jobLaunchRequest).build();

		logger.info("Returning Message<JobLaunchRequest>: {}", jbrMessage.toString());

		return jbrMessage;
	}
}
