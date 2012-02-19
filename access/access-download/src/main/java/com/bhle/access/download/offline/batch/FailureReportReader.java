package com.bhle.access.download.offline.batch;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

@Component
public class FailureReportReader implements ItemReader<FailureReport> {

	private static final Logger logger = LoggerFactory
			.getLogger(FailureReportReader.class);
	
	private StepExecution stepExecution;

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}
	
	public FailureReport read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		if (stepExecution.getReadCount() == 1){
			return null;
		}
		
		FailureReport report = new FailureReport();
		JobParameters jobParameters = stepExecution.getJobParameters();
		report.setGuid(jobParameters.getString("guid"));
		report.setEmail(jobParameters.getString("email"));
		report.setTimestamp(new Date(jobParameters.getLong("timestamp")));
		return report;
	}

}
