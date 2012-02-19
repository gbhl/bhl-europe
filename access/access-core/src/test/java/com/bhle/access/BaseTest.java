package com.bhle.access;

import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.bhle.access.util.FedoraUtil;
import com.ibm.icu.impl.Assert;

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class BaseTest implements ResourceLoaderAware {

	private static final Logger logger = LoggerFactory
			.getLogger(BaseTest.class);

	private static ResourceLoader resourceLoader;

	public void setResourceLoader(ResourceLoader resourceLoader) {
		BaseTest.resourceLoader = resourceLoader;
	}

	@Before
	public void init() throws IOException {
		initializeJmsListeners();
		ingestSamples();
	}

	private void initializeJmsListeners() {
		try {
			logger.info("Starting JMS listeners...");
			Thread.sleep(1000);
			logger.info("Done");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void ingestSamples() throws IOException {
		logger.info("Ingesting samples...");
		Resource monograhObject = resourceLoader
				.getResource("classpath:com/bhle/access/sample/bhle_10706-a00000000000132805961115.xml");
		Resource pageObject = resourceLoader
				.getResource("classpath:com/bhle/access/sample/bhle_10706-a00000000000132805961115-00001.xml");
		FedoraUtil.ingestFOXML(monograhObject.getFile());
		FedoraUtil.ingestFOXML(pageObject.getFile());
		logger.info("Done");
	}

	@After
	public void destroy() {
		logger.info("Purging samples...");
		FedoraUtil.purgeObject("bhle:10706-a00000000000132805961115");
		FedoraUtil.purgeObject("bhle:10706-a00000000000132805961115-00001");
		List<String> pids = FedoraUtil.getAllObjectsPids();
		Assert.assrt(!pids.contains("bhle:10706-a00000000000132805961115"));
		Assert.assrt(!pids
				.contains("bhle:10706-a00000000000132805961115-00001"));
		logger.info("Done");
	}

}
