package com.bhle.access.batch;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bhle.access.convert.AfterBatchIngestConverterManager;
import com.bhle.access.convert.AfterBatchIngestConvertor;
import com.bhle.access.domain.DigitalObjectFactory;
import com.bhle.access.domain.DigitalObjectWrapper;
import com.bhle.access.util.FedoraUtil;
import com.yourmediashelf.fedora.client.FedoraClient;

@Component
public class AfterBatchIngestConverterReader implements
		ItemReader<AfterBatchIngestConvertor> {

	private static List<AfterBatchIngestConvertor> converters;

	private Iterator<AfterBatchIngestConvertor> iterator;

	@Override
	public AfterBatchIngestConvertor read() throws Exception,
			UnexpectedInputException, ParseException,
			NonTransientResourceException {
		if (converters == null) {
			converters = AfterBatchIngestConverterManager.getConverters();
			iterator = converters.iterator();
		}

		if (iterator.hasNext()) {
			return iterator.next();
		} else {
			converters = null;
			return null;
		}
	}
}
