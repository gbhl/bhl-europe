package com.bhle.ingest.batch;

import java.io.File;
import java.net.URI;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.bhle.ingest.Sip;

@Component
public class SipReader implements ItemReader<File> {

	private StepExecution stepExecution;

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	private Sip sip;
	
	@Override
	public File read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		if (sip == null) {
			URI sipUri = URI.create(stepExecution.getJobParameters().getString(
					Sip.JOB_PARAM_URI_KEY));
			sip = new Sip(sipUri);
		}
		return sip.getItems().iterator().next();
	}
}
