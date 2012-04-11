package com.bhle.access.integration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.NoSuchJobException;
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
	private JobLocator jobLocator;

	@Transformer
	public Message<JobLaunchRequest> transform(Message<String> pidMessage) {
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addString(JOB_PARAM_PID_KEY,
				pidMessage.getPayload());
		JobParameters jobParameters = jobParametersBuilder.toJobParameters();

		Job job = null;
		try {
			job = jobLocator.getJob("generateDerivatives");
		} catch (NoSuchJobException e) {
			e.printStackTrace();
		}
		JobLaunchRequest jobLaunchRequest = new JobLaunchRequest(job,
				jobParameters);
		return MessageBuilder.withPayload(jobLaunchRequest).build();
	}

}
