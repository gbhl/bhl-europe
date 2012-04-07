package com.bhle.access.batch;

import java.util.List;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.bhle.access.convert.AfterBatchIngestConvertor;

@Component
@Scope("step")
public class PackageDerivativeWriter implements
		ItemWriter<AfterBatchIngestConvertor> {

	private static final String JOB_PARAM_PID_KEY = "PID";

	private StepExecution stepExecution;

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	@Override
	public void write(List<? extends AfterBatchIngestConvertor> convertors)
			throws Exception {
		String pid = stepExecution.getJobParameters().getString(
				JOB_PARAM_PID_KEY);
		String guid = pid.split("-")[1];
		for (AfterBatchIngestConvertor converter : convertors) {
			converter.convert(guid);
		}

	}

}
