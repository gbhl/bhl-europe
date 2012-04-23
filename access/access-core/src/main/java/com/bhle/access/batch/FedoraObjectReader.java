package com.bhle.access.batch;

import java.util.List;

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

	public GenerationExecutionDecider decider;

	public void setDecider(GenerationExecutionDecider decider) {
		this.decider = decider;
	}

	@BeforeStep
	public void init(StepExecution stepExecution) {
		// this.stepExecution = stepExecution;
		this.pids = FedoraUtil.getAllMembersOfPid(pid);
		this.decider.init();
	}

	public DigitalObjectWrapper read() throws Exception,
			UnexpectedInputException, ParseException {
		if (index <= pids.length - 1) {
			String pid = pids[index].split("/")[1];
			index++;
			logger.info("Reading: {}", pid);
			DigitalObjectWrapper digitalObject = DigitalObjectFactory
					.build(pid);
			setContentModelsToDecider(digitalObject);
			return digitalObject;
		}
		return null;

	}

	private void setContentModelsToDecider(DigitalObjectWrapper digitalObject) {
		List<String> contentModels = digitalObject.getConternModels();
		decider.addContentModels(contentModels);
	}
}
