package com.bhle.ingest.batch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.Message;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

import com.bhle.ingest.Sip;

@Component
public class BatchIngestListener {
	
	@Value("${ingest.done.filename}")
	private String INGEST_DONE_FILENAME;

	@AfterJob
	public void afterJob(JobExecution jobExecution) {
		URI path = URI.create(jobExecution.getJobInstance().getJobParameters()
				.getString(Sip.JOB_PARAM_URI_KEY));
		File directory = new File(path);
		File ingestDoneFile = new File(directory, INGEST_DONE_FILENAME);
		try {
			FileUtils.writeStringToFile(ingestDoneFile, jobExecution.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
