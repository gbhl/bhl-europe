package com.bhle.ingest;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bhle.ingest.batch.IngestException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class IngestServiceTest implements ResourceLoaderAware {

	private ResourceLoader resourceLoader;

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Autowired
	private IngestServiceImpl serviceImpl;

	@Autowired
	private IngestService service;

	@Test
	public void testIngestServiceImpl() throws IOException, IngestException {
		Resource testMetsResource = resourceLoader
				.getResource("classpath:com/bhle/ingest/test_9999.xml");
		File testMets = testMetsResource.getFile();
		int statusCode = serviceImpl.ingestMETS(MessageBuilder.withPayload(
				testMets).build());
		assertEquals(201, statusCode);

		statusCode = serviceImpl.purge("test:9999");
		assertEquals(200, statusCode);
	}

	@Test
	public void testIngestServiceGateway() throws IOException, IngestException{
		Resource testMetsResource = resourceLoader.getResource("classpath:com/bhle/ingest/test_9999.xml");
		File testMets = testMetsResource.getFile();
		int statusCode =  service.ingestItem(testMets);
		assertEquals(201, statusCode);
	}
}
