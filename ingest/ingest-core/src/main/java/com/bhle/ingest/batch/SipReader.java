package com.bhle.ingest.batch;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bhle.ingest.Sip;

@Component
@Scope("step")
public class SipReader implements ItemReader<File> {

	private StepExecution stepExecution;

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	private Iterator<File> iterator;

	@Override
	public File read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {

		if (iterator == null) {
			String guid = stepExecution.getJobParameters().getString(
					Sip.JOB_PARAM_GUID_KEY);
			URI uri = URI.create(stepExecution.getJobParameters().getString(
					Sip.JOB_PARAM_URI_KEY));
			Sip sip = new Sip(guid, uri);
			List<File> items = sip.getItems();
			if (items.isEmpty()) {
				throw new UnexpectedInputException("SIP has no item");
			}
			iterator = sip.getItems().iterator();
		}
		if (iterator.hasNext()) {
			File file = iterator.next();
			return file;
		} else {
			iterator = null;
			return null;
		}
	}
}
