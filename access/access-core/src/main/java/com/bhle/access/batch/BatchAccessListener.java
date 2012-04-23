package com.bhle.access.batch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;


public class BatchAccessListener {

	private static final Logger logger = LoggerFactory
			.getLogger(BatchAccessListener.class);


	@AfterJob
	public void afterJob(JobExecution jobExecution) {

		if (jobExecution.getStatus().isUnsuccessful()) {
			StringBuffer exceptions = new StringBuffer();
			List<Throwable> throwables = jobExecution.getAllFailureExceptions();
			for (Throwable throwable : throwables) {
				exceptions.append(throwable.toString());
			}
			
			logger.info("Access Exception", exceptions.toString());
		}
	}
	
	
}
