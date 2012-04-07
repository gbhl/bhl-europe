package com.bhle.access.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bhle.access.convert.ConverterManager;
import com.bhle.access.domain.Derivative;
import com.bhle.access.domain.DigitalObjectWrapper;

@Component
@Scope("step")
public class SingleObjectConversionProcessor implements
		ItemProcessor<DigitalObjectWrapper, Derivative[]> {

	public Derivative[] process(DigitalObjectWrapper item) throws Exception {
		return ConverterManager.derive(item);
	}

}
