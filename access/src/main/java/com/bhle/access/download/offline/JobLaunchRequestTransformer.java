package com.bhle.access.download.offline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.Message;
import org.springframework.integration.MessageHeaders;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;

public class JobLaunchRequestTransformer {
	
	private static final Logger logger = LoggerFactory
			.getLogger(JobLaunchRequestTransformer.class);

	@Autowired
	@Qualifier("offlineProcessing")
	private Job job;

	@Transformer
	public Message<JobLaunchRequest> tramsformRawRequestToJobLaunchRequest(
			Message<RawRequest> rawRequestMessage) {
		RawRequest rawRequest = rawRequestMessage.getPayload();
		
		logger.info("Transform request from: " + rawRequest.getEmail());
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addString("email", rawRequest.getEmail());
		jobParametersBuilder.addString("guid", rawRequest.getGuid());
		jobParametersBuilder.addString("format", rawRequest.getFormat());
		jobParametersBuilder.addString("ranges", rawRequest.getRanges());
		jobParametersBuilder.addString("resolution", rawRequest.getResolution());
		jobParametersBuilder.addLong("timestamp", (Long)rawRequestMessage.getHeaders().get(MessageHeaders.TIMESTAMP));
		JobParameters jobParameters = jobParametersBuilder.toJobParameters();
		
		JobLaunchRequest jobLaunchRequest = new JobLaunchRequest(job, jobParameters);
		return MessageBuilder.withPayload(jobLaunchRequest).build();
	}
}
