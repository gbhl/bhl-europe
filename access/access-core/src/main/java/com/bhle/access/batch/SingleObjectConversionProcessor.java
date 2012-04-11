package com.bhle.access.batch;

import org.springframework.batch.item.ItemProcessor;

import com.bhle.access.convert.ConverterManager;
import com.bhle.access.domain.Derivative;
import com.bhle.access.domain.DigitalObjectWrapper;

public class SingleObjectConversionProcessor implements
		ItemProcessor<DigitalObjectWrapper, Derivative[]> {

	public Derivative[] process(DigitalObjectWrapper item) throws Exception {
		return ConverterManager.derive(item);
	}

}
