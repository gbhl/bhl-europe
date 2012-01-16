package com.bhle.access.util;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class FedoraUtilTest {

	private static final Logger logger = LoggerFactory
			.getLogger(FedoraUtilTest.class);

	@Test
	public void testGetAllObjects() {
		List<String> pids = FedoraUtil.getAllObjectsPids();
		for (String pid : pids) {
			logger.info("Pid: " + pid);
		}
		Assert.assertTrue(pids.size() >= 4);
	}
	
	
}
