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

import com.bhle.ingest.Sip;

public class SipReader implements ItemReader<File> {

	@BeforeStep
	public void init(StepExecution stepExecution) {
		// this.stepExecution = stepExecution;
		Sip sip = new Sip(guid, URI.create(uri));
		List<File> items = sip.getItems();
		if (items.isEmpty()) {
			throw new UnexpectedInputException("SIP has no item");
		}
		iterator = sip.getItems().iterator();

		batchIngestTracker.init();
	}

	private BatchIngestTracker batchIngestTracker;

	public void setBatchIngestTracker(BatchIngestTracker batchIngestTracker) {
		this.batchIngestTracker = batchIngestTracker;
	}

	private Iterator<File> iterator;

	private String guid;
	private String uri;

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	@Override
	public File read() throws Exception, UnexpectedInputException,
			ParseException, NonTransientResourceException {
		if (iterator != null && iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}
}
