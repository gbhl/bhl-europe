package com.bhle.ingest.integration;

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

import com.bhle.ingest.Sip;

@Component
public class JobLaunchRequestTransformer {

	@Autowired
	private JobLocator jobLocator;

	@Transformer
	public Message<JobLaunchRequest> transform(Message<Sip> sipMessage) {
		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addString(Sip.JOB_PARAM_GUID_KEY, sipMessage
				.getPayload().getGuid());
		jobParametersBuilder.addString(Sip.JOB_PARAM_URI_KEY, sipMessage
				.getPayload().getURI().toString());
		JobParameters jobParameters = jobParametersBuilder.toJobParameters();

		Job job = null;
		try {
			job = jobLocator.getJob("batchIngestJob");
		} catch (NoSuchJobException e) {
			e.printStackTrace();
		}
		JobLaunchRequest jobLaunchRequest = new JobLaunchRequest(job,
				jobParameters);
		return MessageBuilder.withPayload(jobLaunchRequest).build();
	}

}
