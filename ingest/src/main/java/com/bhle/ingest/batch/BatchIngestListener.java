package com.bhle.ingest.batch;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bhle.ingest.Sip;

@Component
public class BatchIngestListener {

	@Value("${ingest.done.filename}")
	private String INGEST_DONE_FILENAME;

	@AfterJob
	public void afterJob(JobExecution jobExecution) {
		writeIngestDoneFile(jobExecution);
	}

	private void writeIngestDoneFile(JobExecution jobExecution) {
		URI path = URI.create(jobExecution.getJobInstance().getJobParameters()
				.getString(Sip.JOB_PARAM_URI_KEY));
		File directory = new File(path);
		File ingestDoneFile = new File(directory, INGEST_DONE_FILENAME);
		try {
			FileUtils.writeLines(ingestDoneFile, jobExecution.getAllFailureExceptions());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
