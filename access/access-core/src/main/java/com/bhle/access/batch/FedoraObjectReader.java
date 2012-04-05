package com.bhle.access.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import com.bhle.access.domain.DigitalObjectFactory;
import com.bhle.access.domain.DigitalObjectWrapper;
import com.bhle.access.util.FedoraUtil;

@Component
public class FedoraObjectReader implements ItemReader<DigitalObjectWrapper> {

	private static final Logger logger = LoggerFactory
			.getLogger(FedoraObjectReader.class);
	
	private String[] pids;

	private static final String JOB_PARAM_PID_KEY = "PID";

	private StepExecution stepExecution;

	private int index;

	@BeforeStep
	public void saveStepExecution(StepExecution stepExecution) {
		this.stepExecution = stepExecution;
	}

	public DigitalObjectWrapper read() throws Exception,
			UnexpectedInputException, ParseException {
		if (pids == null) {
			String pid = stepExecution.getJobParameters().getString(
					JOB_PARAM_PID_KEY);
			pids = FedoraUtil.getAllMemberOfPid(pid);
			index = 0;
		}

		if (index > pids.length - 1) {
			pids = null;
			index = 0;
			return null;
		}

		String pid = pids[index].split("/")[1];
		index++;
		logger.info("Reading: {}", pid);
		return DigitalObjectFactory.build(pid);
	}
}
