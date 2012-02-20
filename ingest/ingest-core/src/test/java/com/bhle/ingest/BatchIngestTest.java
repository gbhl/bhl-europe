package com.bhle.ingest;

import java.io.IOException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class BatchIngestTest implements ResourceLoaderAware {

	@Value("${ingest.directory.name}")
	private String ingest_directory_name;

	@Value("${ingest.running.filename}")
	private String ingest_running_filename;

	@Value("${ingest.done.filename}")
	private String ingest_done_filename;

	private static final Logger logger = LoggerFactory
			.getLogger(BatchIngestTest.class);

	private static ResourceLoader resourceLoader;

	public void setResourceLoader(ResourceLoader resourceLoader) {
		BatchIngestTest.resourceLoader = resourceLoader;
	}

	@Autowired
	private FedoraServiceImpl service;

	@Before
	public void init() {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testExistenceOfSampleByPurge() {
		int statusCode = service.purge("bhle:10706-a00000000000132805961115");
		Assert.assertEquals(200, statusCode);
	}

	@After
	public void destroy() {
		Resource ingestRunningFile = resourceLoader
				.getResource("classpath:/com/bhle/ingest/"
						+ ingest_directory_name + "/" + ingest_running_filename);
		Resource ingestDoneFile = resourceLoader
				.getResource("classpath:/com/bhle/ingest/"
						+ ingest_directory_name + "/" + ingest_done_filename);

		try {
			ingestRunningFile.getFile().delete();
			ingestDoneFile.getFile().delete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
