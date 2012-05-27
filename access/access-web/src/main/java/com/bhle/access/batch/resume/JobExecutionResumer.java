package com.bhle.access.batch.resume;

import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.admin.service.JobService;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;

public class JobExecutionResumer {

	private static final Logger logger = LoggerFactory
			.getLogger(JobExecutionResumer.class);

	@Autowired
	private JobExplorer explorer;

	@Autowired
	private JobRepository repo;

	@Autowired
	private JobService service;

	private Set<JobExecution> getAllRunningJobExecutions() {
		return explorer.findRunningJobExecutions("generateDerivatives");
	}

	private void forceJobToFail(JobExecution jobExecution) {
		jobExecution.setEndTime(new Date());
		jobExecution.setStatus(BatchStatus.FAILED);

		repo.update(jobExecution);
	}

	private void restartJob(JobExecution jobExecution) {
		try {
			service.restart(jobExecution.getId());
		} catch (NoSuchJobExecutionException e) {
			e.printStackTrace();
		} catch (JobExecutionAlreadyRunningException e) {
			e.printStackTrace();
		} catch (JobRestartException e) {
			e.printStackTrace();
		} catch (JobInstanceAlreadyCompleteException e) {
			e.printStackTrace();
		} catch (NoSuchJobException e) {
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			e.printStackTrace();
		}
	}

	public void resumeAllInteruptedRunningJobs() {
		for (JobExecution jobExecution : getAllRunningJobExecutions()) {
			forceJobToFail(jobExecution);
			restartJob(jobExecution);
		}
	}
}
