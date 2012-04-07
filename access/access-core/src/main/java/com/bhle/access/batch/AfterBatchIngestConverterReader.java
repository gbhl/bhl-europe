package com.bhle.access.batch;

import java.util.Iterator;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bhle.access.convert.AfterBatchIngestConverterManager;
import com.bhle.access.convert.AfterBatchIngestConvertor;

@Component
@Scope("step")
public class AfterBatchIngestConverterReader implements
		ItemReader<AfterBatchIngestConvertor> {

	private Iterator<AfterBatchIngestConvertor> iterator;

	@Override
	public AfterBatchIngestConvertor read() throws Exception,
			UnexpectedInputException, ParseException,
			NonTransientResourceException {
		if (iterator == null) {
			iterator = AfterBatchIngestConverterManager.getConverters()
					.iterator();
		}

		if (iterator.hasNext()) {
			return iterator.next();
		} else {
			iterator = null;
			return null;
		}
	}
}
