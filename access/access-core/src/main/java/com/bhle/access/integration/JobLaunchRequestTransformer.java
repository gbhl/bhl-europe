package com.bhle.access.integration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.integration.launch.JobLaunchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class JobLaunchRequestTransformer {

	private static final String JOB_PARAM_PID_KEY = "PID";

	@Autowired
	@Qualifier("generateDerivatives")
	private Job generateDerivativesJob;

	@Transformer
	public Message<JobLaunchRequest> transform(Message<String> pidMessage) {
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addString(JOB_PARAM_PID_KEY,
				pidMessage.getPayload());
		JobParameters jobParameters = jobParametersBuilder.toJobParameters();

		JobLaunchRequest jobLaunchRequest = new JobLaunchRequest(
				generateDerivativesJob, jobParameters);
		return MessageBuilder.withPayload(jobLaunchRequest).build();
	}

}
