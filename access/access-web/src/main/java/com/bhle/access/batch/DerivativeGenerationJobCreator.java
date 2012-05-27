package com.bhle.access.batch;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

public class DerivativeGenerationJobCreator implements ItemWriter<String> {

	private static final String JOB_PARAM_PID_KEY = "PID";

	private JobLocator jobLocator;

	public void setJobLocator(JobLocator jobLocator) {
		this.jobLocator = jobLocator;
	}

	private JobLauncher jobLauncher;

	public void setJobLauncher(JobLauncher jobLauncher) {
		this.jobLauncher = jobLauncher;
	}

	@Override
	public void write(List<? extends String> pids) throws Exception {
		Job derivativeGenerationJob = jobLocator.getJob("generateDerivatives");

		for (String pid : pids) {
			JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
			jobParametersBuilder.addString(JOB_PARAM_PID_KEY, pid);
			JobParameters jobParameters = jobParametersBuilder
					.toJobParameters();
			jobLauncher.run(derivativeGenerationJob, jobParameters);
		}
	}

}
