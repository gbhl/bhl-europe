package com.bhle.ingest;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

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
import org.springframework.test.annotation.ExpectedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bhle.ingest.jms.PreingestJmsProducerStub;
import com.yourmediashelf.fedora.client.FedoraClientException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class RollbackTest implements ResourceLoaderAware {

	private static final Logger logger = LoggerFactory
			.getLogger(BatchIngestTest.class);

	private static ResourceLoader resourceLoader;

	public void setResourceLoader(ResourceLoader resourceLoader) {
		RollbackTest.resourceLoader = resourceLoader;
	}

	@Autowired
	private PreingestJmsProducerStub jmsProducer;

	@Value("${jms.message.guid}")
	private String MSG_GUID_NAME;
	@Value("${jms.message.uri}")
	private String MSG_URI_NAME;

	@Autowired
	private FedoraServiceImpl service;

	@Before
	public void init() throws Exception {
		Map<String, String> messageBody = new HashMap<String, String>();
		logger.info("Create Test Message...");

		messageBody.put(MSG_GUID_NAME, "bhle:10706/a000test");
		logger.info("MessageBody: {}:{}", MSG_GUID_NAME, "bhle:10706/a000test");

		Resource aip = resourceLoader
				.getResource("classpath:/com/bhle/ingest/badaip");
		messageBody.put(MSG_URI_NAME, aip.getURI().toString());
		logger.info("MessageBody: {}:{}", MSG_URI_NAME, aip.getURI().toString());

		jmsProducer.send(messageBody);

		Thread.sleep(20000);
	}

	@Test
	public void testExistenceOfSampleByPurge() throws InterruptedException {
		int statusCode = service.purge("bhle:10706-a000test");
		statusCode = service.purge("bhle:10706-a000test-00001");
		statusCode = service.purge("bhle:10706-a000test-00002");
	}
}