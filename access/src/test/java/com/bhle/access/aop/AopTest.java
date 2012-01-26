package com.bhle.access.aop;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class AopTest {
	
	private static final Logger logger = LoggerFactory.getLogger(AopTest.class);
	
	@Autowired(required = true)
	private DownloadStub downloadStub;

	@Test
	public void testAroundAspect() {
		String result = downloadStub.downloadSomething();
		logger.info("Received result: " + result);
	}
}
