package com.bhle.access.jms;

import org.junit.Ignore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import com.bhle.access.BaseTest;

@Ignore
@ContextConfiguration
public class AfterBatchIngestJmsTest extends BaseTest {

	@Autowired
	protected JmsProducer jmsProducer;
}
