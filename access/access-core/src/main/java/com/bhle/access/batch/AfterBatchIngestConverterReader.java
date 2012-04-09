package com.bhle.access.batch;

import java.util.Iterator;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.bhle.access.convert.AfterBatchIngestConverterManager;
import com.bhle.access.convert.AfterBatchIngestConvertor;
import com.bhle.access.util.FedoraUtil;

public class AfterBatchIngestConverterReader implements
		ItemReader<AfterBatchIngestConvertor> {

	private Iterator<AfterBatchIngestConvertor> iterator;

	@BeforeStep
	public void init(StepExecution stepExecution) {
		// this.stepExecution = stepExecution;
		iterator = AfterBatchIngestConverterManager.getConverters().iterator();
	}

	@Override
	public AfterBatchIngestConvertor read() throws Exception,
			UnexpectedInputException, ParseException,
			NonTransientResourceException {
		if (iterator.hasNext()) {
			return iterator.next();
		}
		return null;
	}
}
