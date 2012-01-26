package com.bhle.access.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@Path("management")
public class Management {
	@Autowired
	private JobLauncher launcer;

	@Autowired
	@Qualifier("updateDerivative")
	private Job updateDerivativeJob;

	@POST
	@Path("updateDerivatives")
	public void updateDerivatives() {
		try {
			launcer.run(updateDerivativeJob, new JobParameters());
		} catch (JobExecutionAlreadyRunningException e) {
			e.printStackTrace();
		} catch (JobRestartException e) {
			e.printStackTrace();
		} catch (JobInstanceAlreadyCompleteException e) {
			e.printStackTrace();
		} catch (JobParametersInvalidException e) {
			e.printStackTrace();
		}
	}

}
