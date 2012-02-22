package com.bhle.access;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;

import com.bhle.access.storage.StorageService;

@ContextConfiguration
public class DerivativesFunctionalTest extends BaseTest {

	private static final Logger logger = LoggerFactory
			.getLogger(DerivativesFunctionalTest.class);

	@Autowired
	private StorageService service;

	@Test
	public void testExistenceOfDerivatives() throws IOException,
			InterruptedException {
		logger.info("testExistenceOfDerivatives");
//		Assert.notNull(service.openDatastream("a00000000000132805961115",
//				"OLEF", null));
//		Assert.notNull(service.openDatastream("a00000000000132805961115",
//				"JP2", "00001"));
	}
}
