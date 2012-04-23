package com.bhle.access.batch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;

public class GenerationExecutionDecider implements JobExecutionDecider {

	private Set<String> contentModels = new HashSet<String>();

	public void addContentModels(String contentModel) {
		contentModels.add(contentModel);
	}

	public void addContentModels(List<String> contentModels) {
		for (String contentModel : contentModels) {
			addContentModels(contentModel);
		}
	}

	@Override
	public FlowExecutionStatus decide(JobExecution jobExecution,
			StepExecution stepExecution) {
		if (!contentModels.contains("info:fedora/bhle-cmodel:pageCModel")) {
			return new FlowExecutionStatus("NO_PAGE");
		}
		return FlowExecutionStatus.COMPLETED;
	}

	public void init() {
		contentModels.clear();
	}
}
