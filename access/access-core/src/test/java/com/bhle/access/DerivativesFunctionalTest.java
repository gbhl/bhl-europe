package com.bhle.access;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.bhle.access.storage.StorageService;

@ContextConfiguration
public class DerivativesFunctionalTest extends BaseTest {
	
	private static final Logger logger = LoggerFactory
			.getLogger(DerivativesFunctionalTest.class);
	
	@Autowired
	private StorageService service;
	
	@Test
	public void testExistenceOfDerivatives() throws IOException, InterruptedException{
		logger.info("testExistenceOfDerivatives");
		Thread.sleep(5000);
		List<URI> guids = service.listGuids();
		for (URI uri : guids) {
			System.out.println(uri);
		}
	}
}
