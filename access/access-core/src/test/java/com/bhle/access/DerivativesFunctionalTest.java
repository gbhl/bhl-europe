package com.bhle.access;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

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
		Thread.sleep(5000);
	}
}
