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

	protected ResourceLoader resourceLoader;

	public String MONOGRAPH_PID = "bhle:10706-a000test";
	public String PAGE_1_PID = "bhle:10706-a000test-00001";
	public String PAGE_2_PID = "bhle:10706-a000test-00002";

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
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
				.getResource("classpath:com/bhle/access/sample/bhle_10706-a000test.xml");
		Resource page1Object = resourceLoader
				.getResource("classpath:com/bhle/access/sample/bhle_10706-a000test-00001.xml");
		Resource page2Object = resourceLoader
				.getResource("classpath:com/bhle/access/sample/bhle_10706-a000test-00002.xml");
		FedoraUtil.ingestFOXML(monograhObject.getInputStream());
		FedoraUtil.ingestFOXML(page1Object.getInputStream());
		FedoraUtil.ingestFOXML(page2Object.getInputStream());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		logger.info("Activate samples...");
		FedoraUtil.activateObject("bhle:10706-a000test");
		logger.info("Done");
	}

	@After
	public void destroy() {
		logger.info("Purging samples...");
		FedoraUtil.purgeObject(MONOGRAPH_PID);
		FedoraUtil.purgeObject(PAGE_1_PID);
		FedoraUtil.purgeObject(PAGE_2_PID);
		List<String> pids = FedoraUtil.getAllObjectsPids();
		Assert.assrt(!pids.contains("bhle:10706-a000test"));
		Assert.assrt(!pids.contains("bhle:10706-a000test-00001"));
		Assert.assrt(!pids.contains("bhle:10706-a000test-00002"));
		logger.info("Done");
	}

}
