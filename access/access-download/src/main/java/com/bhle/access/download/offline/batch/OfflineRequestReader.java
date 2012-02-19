package com.bhle.access.download.offline.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.bhle.access.download.PageURIExtractor;
import com.bhle.access.download.PageURIExtractorImpl;

@Component
public class OfflineRequestReader implements ItemReader<String[]> {

	private static final Logger logger = LoggerFactory
			.getLogger(OfflineRequestReader.class);
	
	private StepExecution stepExecution;

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}
	
	private PageURIExtractor rangesExtractor = new PageURIExtractorImpl();

	public String[] read() throws Exception, UnexpectedInputException, ParseException {
		logger.info("Reading Job Request");
		
		if (stepExecution.getReadCount() == 1){
			return null;
		}
		JobParameters jobParameters = stepExecution.getJobParameters();
		
		String guid = jobParameters.getString("guid");
		String ranges = jobParameters.getString("ranges");
		
		return rangesExtractor.getPageURIs(guid, ranges);
	}
}
