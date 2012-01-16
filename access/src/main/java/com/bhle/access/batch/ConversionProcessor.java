package com.bhle.access.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.bhle.access.convert.ConvertorManager;
import com.bhle.access.domain.Derivative;
import com.bhle.access.domain.DigitalObjectWrapper;

@Component
public class ConversionProcessor implements
		ItemProcessor<DigitalObjectWrapper, Derivative[]> {

	public Derivative[] process(DigitalObjectWrapper item) throws Exception {
		return ConvertorManager.derive(item);
	}

}
