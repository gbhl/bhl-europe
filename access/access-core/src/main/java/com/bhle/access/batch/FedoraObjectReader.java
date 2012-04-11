package com.bhle.access.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import com.bhle.access.domain.DigitalObjectFactory;
import com.bhle.access.domain.DigitalObjectWrapper;
import com.bhle.access.util.FedoraUtil;

public class FedoraObjectReader implements ItemReader<DigitalObjectWrapper> {

	private static final Logger logger = LoggerFactory
			.getLogger(FedoraObjectReader.class);

	private String[] pids;

	private int index = 0;

	private String pid;

	public void setPid(String pid) {
		this.pid = pid;
	}

	@BeforeStep
	public void init(StepExecution stepExecution) {
		// this.stepExecution = stepExecution;
		pids = FedoraUtil.getAllMemberOfPid(pid);
	}

	public DigitalObjectWrapper read() throws Exception,
			UnexpectedInputException, ParseException {
		if (index <= pids.length - 1) {
			String pid = pids[index].split("/")[1];
			index++;
			logger.info("Reading: {}", pid);
			return DigitalObjectFactory.build(pid);
		}
		return null;

	}
}
